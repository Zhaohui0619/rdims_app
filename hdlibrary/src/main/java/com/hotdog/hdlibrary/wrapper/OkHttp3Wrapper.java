package com.hotdog.hdlibrary.wrapper;

import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.hotdog.hdlibrary.HDRuntimeContext;
import com.hotdog.hdlibrary.encrypt.Digest;
import com.hotdog.hdlibrary.http.HDHttpClient;
import com.hotdog.hdlibrary.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttp3Wrapper {

	public static final int TIME_OUT = 30_000;
	public static final int CACHE_SIZE = 1024 * 1024 * 100;

    private static OkHttpClient cachePreferredSingleton = null;
    private static OkHttpClient httpCacheSingleton = null;
	private static OkHttpClient fileCachePreferredSingleton = null;
    private static OkHttpClient refreshPreferredSingleton = null;
    private static OkHttpClient imageLastModifiedSupportSingleton = null;
    private static OkHttpClient fileLastModifiedSupportSingleton = null;

    private static File httpImageCacheDir = null;
    private static File httpCacheDir = null;
    private static File fileCacheDir = null;

	private static File getImageCacheDir() {
		if (httpImageCacheDir == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (httpImageCacheDir == null) {
					httpImageCacheDir = new File(HDRuntimeContext.get().getCacheDir(), "image");
				}
			}
		}
		if (!httpImageCacheDir.exists()) {
			httpImageCacheDir.mkdirs();
		}
		return httpImageCacheDir;
	}

	private static File getHttpCacheDir() {
		if (httpCacheDir == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (httpCacheDir == null) {
					httpCacheDir = new File(HDRuntimeContext.get().getCacheDir(), "http");
				}
			}
		}
		if (!httpCacheDir.exists()) {
			httpCacheDir.mkdirs();
		}
		return httpCacheDir;
	}
	
	private static File getFileCacheDir() {
		if (fileCacheDir == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (fileCacheDir == null) {
					fileCacheDir = new File(HDRuntimeContext.get().getCacheDir(), "file");
				}
			}
		}
		if (!fileCacheDir.exists()) {
			fileCacheDir.mkdirs();
		}
		return fileCacheDir;
	}

	private static String urlToKey(String url) {
		return Digest.md5Hex(url.getBytes());
	}

	//  Interceptor declaration
    private static final Interceptor SERVER_RESPONSE_CACHE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            //  The response from server
            //  tolerate 1-year stale
            int maxStale = 60 * 60 * 24 * 365;

            if (originalResponse.code() != 200) {
            }

	        return originalResponse.newBuilder()
	                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
	                .build();

            //  If response code is 304, the originalResponse body is empty.
            //  We have to issue another request in onError of Picasso request to fetch from cache.
            //  Refer to Utils:fillRemoteImage
        }
    };

	//  Interceptor declaration  file
    private static final Interceptor SERVER_FILE_RESPONSE_CACHE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());

            //  The response from server
            //  tolerate 1-year stale
            int maxStale = 60 * 60 * 24 * 365;

            if (originalResponse.code() == 304) {
                return originalResponse.newBuilder()
                        .code(200)
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }

	        return originalResponse.newBuilder()
	                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
	                .build();

            //  If response code is 304, the originalResponse body is empty.
            //  We have to issue another request in onError of Picasso request to fetch from cache.
            //  Refer to Utils:fillRemoteImage
        }
    };

	private static final Interceptor CLIENT_REQUEST_CACHE_PREFERRED_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {
			Request request = chain.request();
			int maxStale = 60 * 60 * 24 * 365;
			request = request.newBuilder()
					.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
					.build();
			return chain.proceed(request);
		}
	};

	private static final Interceptor CLIENT_IMAGE_LAST_MODIFIED_SUPPORT_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {
			Request request = chain.request();
			int maxStale = 60 * 60 * 24 * 365;

			try {
				String key = urlToKey(request.url().toString());
				File cacheDir = OkHttp3Wrapper.getImageCacheDir();
				File cacheFile = new File(cacheDir.getPath(), key + ".0");
				FileInputStream fis = new FileInputStream(cacheFile);
				int length = fis.available();
				byte[] buffer = new byte[length];
				fis.read(buffer);
				String bufferStr = new String(buffer, "UTF-8");

				List<String> result = Splitter.on("\n").trimResults().omitEmptyStrings().splitToList(bufferStr);
				Iterable<String> lastModifiedIterable = Iterables.filter(result, Predicates.containsPattern("Last-Modified"));
				String lastModifiedPair = Iterables.getLast(lastModifiedIterable);
				Iterable<String> lastModifiedValueIterable = Splitter.on(':').limit(2).split(lastModifiedPair);
				String lastModified = Iterables.getLast(lastModifiedValueIterable);

				fis.close();

				if (lastModified != null) {
					request = request.newBuilder()
							.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
							.header("If-Modified-Since", lastModified)
							.build();
				} else {
					request = request.newBuilder()
							.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
							.build();
				}
			} catch (Exception e) {
				request = request.newBuilder()
						.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
						.build();
			}
			return chain.proceed(request);
		}
	};
	
	private static final Interceptor CLIENT_FILE_LAST_MODIFIED_SUPPORT_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {
			Request request = chain.request();
			int maxStale = 60 * 60 * 24 * 365;

			try {
				String key = urlToKey(request.url().toString());
				File cacheDir = OkHttp3Wrapper.getFileCacheDir();
				File cacheFile = new File(cacheDir.getPath(), key + ".0");
				FileInputStream fis = new FileInputStream(cacheFile);
				int length = fis.available();
				byte[] buffer = new byte[length];
				fis.read(buffer);
				String bufferStr = new String(buffer, "UTF-8");

				List<String> result = Splitter.on("\n").trimResults().omitEmptyStrings().splitToList(bufferStr);
				Iterable<String> lastModifiedIterable = Iterables.filter(result, Predicates.containsPattern("Last-Modified"));
				String lastModifiedPair = Iterables.getLast(lastModifiedIterable);
				Iterable<String> lastModifiedValueIterable = Splitter.on(':').limit(2).split(lastModifiedPair);
				String lastModified = Iterables.getLast(lastModifiedValueIterable);

				fis.close();

				if (lastModified != null) {
					request = request.newBuilder()
							.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
							.header("If-Modified-Since", lastModified)
							.build();
				} else {
					request = request.newBuilder()
							.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
							.build();
				}
			} catch (Exception e) {
				request = request.newBuilder()
						.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
						.build();
			}
			return chain.proceed(request);
		}
	};

	private static final Interceptor CLIENT_REQUEST_REFRESH_PREFERRED_INTERCEPTOR = new Interceptor() {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {
			Request request = chain.request();
			if (NetUtils.isNetworkAvailable()) {
				int maxAge = 60;
				request = request.newBuilder()
						.cacheControl(new CacheControl.Builder().maxAge(maxAge, TimeUnit.SECONDS).build())
						.build();
			} else {
				int maxStale = 60 * 60 * 24 * 365;
				request = request.newBuilder()
						.cacheControl(new CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())
						.build();
			}
			return chain.proceed(request);
		}
	};

    public static OkHttpClient getRefreshPreferredClientInstance() {
        if (refreshPreferredSingleton == null) {
            synchronized (OkHttp3Wrapper.class) {
                if (refreshPreferredSingleton == null) {
                    refreshPreferredSingleton = HDHttpClient.newClientBuilder()
                            .cache(new Cache(OkHttp3Wrapper.getImageCacheDir(), CACHE_SIZE))
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .addInterceptor(CLIENT_REQUEST_REFRESH_PREFERRED_INTERCEPTOR)
                            .addNetworkInterceptor(SERVER_RESPONSE_CACHE_INTERCEPTOR)
                            .build();
                }
            }
        }
        return refreshPreferredSingleton;
    }

	public static OkHttpClient getCachePreferredClientInstance() {
		if (cachePreferredSingleton == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (cachePreferredSingleton == null) {
					cachePreferredSingleton = HDHttpClient.newClientBuilder()
							.cache(new Cache(OkHttp3Wrapper.getImageCacheDir(), CACHE_SIZE))
							.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
							.readTimeout(TIME_OUT, TimeUnit.SECONDS)
							.addInterceptor(CLIENT_REQUEST_CACHE_PREFERRED_INTERCEPTOR)
							.addNetworkInterceptor(SERVER_RESPONSE_CACHE_INTERCEPTOR)
							.build();
				}
			}
		}
		return cachePreferredSingleton;
	}

	public static OkHttpClient getImageLastModifiedSupportClientInstance() {
		if (imageLastModifiedSupportSingleton == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (imageLastModifiedSupportSingleton == null) {
					imageLastModifiedSupportSingleton = HDHttpClient.newClientBuilder()
							.cache(new Cache(OkHttp3Wrapper.getImageCacheDir(), CACHE_SIZE))
							.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
							.readTimeout(TIME_OUT, TimeUnit.SECONDS)
							.addInterceptor(CLIENT_IMAGE_LAST_MODIFIED_SUPPORT_INTERCEPTOR)
							.addNetworkInterceptor(SERVER_RESPONSE_CACHE_INTERCEPTOR)
							.build();
				}
			}
		}
		return imageLastModifiedSupportSingleton;
	}
	
	public static OkHttpClient getFileLastModifiedSupportClientInstance() {
		if (fileLastModifiedSupportSingleton == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (fileLastModifiedSupportSingleton == null) {
					fileLastModifiedSupportSingleton = HDHttpClient.newClientBuilder()
							.cache(new Cache(OkHttp3Wrapper.getFileCacheDir(), CACHE_SIZE))
							.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
							.readTimeout(TIME_OUT, TimeUnit.SECONDS)
							.addInterceptor(CLIENT_FILE_LAST_MODIFIED_SUPPORT_INTERCEPTOR)
							.addNetworkInterceptor(SERVER_RESPONSE_CACHE_INTERCEPTOR)
							.build();
				}
			}
		}
		return fileLastModifiedSupportSingleton;
	}

	//文件下载的过滤Interceptor，请求modify，如果服务器未改动，返回304，使用本地数据
    public static OkHttpClient getFileCachePreferredClientInstance() {
        if (fileCachePreferredSingleton == null) {
            synchronized (OkHttp3Wrapper.class) {
                if (fileCachePreferredSingleton == null) {
	                fileCachePreferredSingleton = HDHttpClient.newClientBuilder()
                            .cache(new Cache(OkHttp3Wrapper.getFileCacheDir(), CACHE_SIZE))
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .addInterceptor(CLIENT_REQUEST_CACHE_PREFERRED_INTERCEPTOR)
                            .addNetworkInterceptor(SERVER_FILE_RESPONSE_CACHE_INTERCEPTOR)
                            .build();
                }
            }
        }
        return fileCachePreferredSingleton;
    }

    private static final Interceptor HTTP_CACHE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response originalResponse = null;

            if (request.method().equals("GET")) {
                CacheControl.Builder cacheBuilder = new CacheControl.Builder();
                cacheBuilder.maxAge(0, TimeUnit.SECONDS);
                cacheBuilder.maxStale(365, TimeUnit.DAYS);
                CacheControl cacheControl = cacheBuilder.build();

                if (!NetUtils.isNetworkAvailable()) {
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                originalResponse = chain.proceed(request);
                if (NetUtils.isNetworkAvailable()) {
                    int maxAge = 0; // read from cache
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public ,max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            } else if (request.method().equals("POST")) {
                String cacheKey = request.header("cacheKey");

                if (NetUtils.isNetworkAvailable()) {
                    originalResponse = chain.proceed(request);
                    if (originalResponse.code() != 200) {//网络请求失败，从cache中读取数据
                        originalResponse = getResponseFromCache(request, cacheKey);
                    } else if (cacheKey != null) {//如果cacheKey不为NULL，即按OKHttp中GET请求方式的缓存方法 缓存到本地
                        String response = putResponse2Cache(originalResponse, cacheKey);
                        return originalResponse.newBuilder()
                                .request(originalResponse.request())
                                .headers(originalResponse.headers())
                                .protocol(originalResponse.protocol())
                                .message(originalResponse.message())
                                .code(originalResponse.code())
                                .body(ResponseBody.create(originalResponse.body().contentType(), response))
                                .sentRequestAtMillis(originalResponse.sentRequestAtMillis())
                                .receivedResponseAtMillis(originalResponse.receivedResponseAtMillis())
                                .build();
                    }
                    return originalResponse;
                } else {//如果没有网路，按OKHttp中GET请求方式的缓存方法 从本地取缓存到response中并返回
                    originalResponse = getResponseFromCache(request, cacheKey);
                }
            }
            return originalResponse;
        }
    };


	/**
	 *
	 * @return 带缓存请求的HttpClient
	 */
	public static OkHttpClient getHttpCacheClientInstance() {
		if (httpCacheSingleton == null) {
			synchronized (OkHttp3Wrapper.class) {
				if (httpCacheSingleton == null) {
					httpCacheSingleton = HDHttpClient.newClientBuilder()
							.cache(new Cache(OkHttp3Wrapper.getHttpCacheDir(), CACHE_SIZE))
							.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
							.readTimeout(TIME_OUT, TimeUnit.SECONDS)
							.addInterceptor(HTTP_CACHE_INTERCEPTOR)
//                            .addNetworkInterceptor(HTTP_CACHE_INTERCEPTOR)
							.build();
				}
			}
		}
		return httpCacheSingleton;
	}

    /**
     * 缓存response到本地cache目录
     *
     * @param response 网络数据
     * @param cacheKey cache key
     */
    private static String putResponse2Cache(Response response, String cacheKey) {
        String content = "";
        try {
            String key = urlToKey(cacheKey);
            //put data to cache file
            File cacheDir = OkHttp3Wrapper.getHttpCacheDir();
            File headerFile = new File(cacheDir.getPath(), key + ".0");
            File bodyFile = new File(cacheDir.getPath(), key + ".1");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            if (!headerFile.exists()) {
                headerFile.createNewFile();
            }
            if (!bodyFile.exists()) {
                bodyFile.createNewFile();
            }

            OutputStreamWriter headerWrite = new OutputStreamWriter(new FileOutputStream(headerFile, false), "utf-8");
            OutputStreamWriter bodyWrite = new OutputStreamWriter(new FileOutputStream(bodyFile, false), "utf-8");

            BufferedWriter bw1 = new BufferedWriter(headerWrite);
            BufferedWriter bw2 = new BufferedWriter(bodyWrite);
            content = response.body().string();
            bw2.write(content);

            JSONArray array = new JSONArray();
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("name", headers.name(i));
                object.put("value", headers.value(i));
                array.put(object);
            }
            //create jsondata (protocol, code, message, headers, requestMillis, responseMillis, Content-Type, Content-Length )
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("protocol", response.protocol().toString());
            jsonObject.put("code", response.code());
            jsonObject.put("message", response.message());
            jsonObject.put("headers", array);
            jsonObject.put("requestMillis", response.sentRequestAtMillis());
            jsonObject.put("responseMillis", response.receivedResponseAtMillis());
            jsonObject.put("Content-Type", response.body().contentType());
            jsonObject.put("Content-Length", response.body().contentLength());

            bw1.write(jsonObject.toString());

            bw1.flush();
            bw1.close();
            bw2.flush();
            bw2.close();
            headerWrite.close();
            bodyWrite.close();

            return content;
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
	    return content;
    }

    /**
     * 从本地cache目录中加载数据组合成默认的response，post请求
     *
     * @param request
     * @param cacheKey
     * @return
     */
    private static Response getResponseFromCache(Request request, String cacheKey) {
        try {
            String key = urlToKey(cacheKey);
            //get data from cache file
            File cacheDir = OkHttp3Wrapper.getHttpCacheDir();
            File headerFile = new File(cacheDir.getPath(), key + ".0");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            if (!headerFile.exists()) {
                headerFile.createNewFile();
            }
            Reader headerReader = new FileReader(headerFile);
            BufferedReader headerBr = new BufferedReader(headerReader);

            String line1 = headerBr.readLine();
            StringBuffer buffer1 = new StringBuffer();
            while (line1 != null) {
                buffer1.append(line1 + "\r\n");
                line1 = headerBr.readLine();
            }
            //parse headerStr data (get protocol, code, message, headers, requestMillis, responseMillis, Content-Type, Content-Length from headerStr jsondata)
            JSONObject jsonObject = new JSONObject(buffer1.toString());
            String protocol = jsonObject.optString("protocol");
            int code = jsonObject.optInt("code");
            String message = jsonObject.optString("message");
            HashMap<String, String> map = new HashMap<String, String>();
            JSONArray array = jsonObject.optJSONArray("headers");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                map.put(object.optString("name"), object.optString("value"));
            }
            long requestMillis = jsonObject.optLong("requestMillis");
            long responseMillis = jsonObject.optLong("responseMillis");
            String contentType = jsonObject.optString("Content-Type");
            String contentLength = jsonObject.optString("Content-Length");

            headerBr.close();
            headerReader.close();


            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.get(protocol))
                    .code(code)
                    .message(message)
                    .headers(Headers.of(map))
                    .body(ResponseBody.create(okhttp3.MediaType.parse(contentType), getDataFromCache(cacheKey)))
//                    .handshake(response.handshake())
                    .sentRequestAtMillis(requestMillis)
                    .receivedResponseAtMillis(responseMillis)
                    .build();

        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
	    return new Response.Builder().build();
    }

	/**
	 * 从本地直接读取缓存的数据
	 * @param cacheKey
	 * @return
	 */
	public static String getDataFromCache(String cacheKey) {
        StringBuilder buffer2 = new StringBuilder();
        String key = urlToKey(cacheKey);
        try {
            File cacheDir = OkHttp3Wrapper.getHttpCacheDir();
            File bodyFile = new File(cacheDir.getPath(), key + ".1");
            if (!bodyFile.exists()) {
                bodyFile.createNewFile();
            }
            Reader bodyReader = new FileReader(bodyFile);
            BufferedReader bodyBr = new BufferedReader(bodyReader);
            String line2 = bodyBr.readLine();
            while (line2 != null) {
                buffer2.append(line2).append("\r\n");
                line2 = bodyBr.readLine();
            }
            bodyBr.close();
            bodyReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return buffer2.toString();
    }

}