package com.hotdog.hdlibrary.http;

import android.content.Context;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hotdog.hdlibrary.HDRuntimeContext;
import com.hotdog.hdlibrary.utils.IOUtils;
import com.hotdog.hdlibrary.wrapper.OkHttp3Wrapper;
import com.orhanobut.logger.Logger;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import bolts.Continuation;
import bolts.Task;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public final class HDHttpClient {
	
	/**
	 * @return 克隆出一个httpClient, 共享连接池
	 */
	public static OkHttpClient.Builder newClientBuilder() {
		return getHttpClient().newBuilder();
	}
	
	/**
	 * 超时时长,单位秒
	 */
	private static final long TIME_OUT = 30;
	
	private static OkHttpClient okHttpClient;
	
	static OkHttpClient getHttpClient() {
		if (okHttpClient == null) {
			synchronized (HDHttpRequest.class) {
				if (okHttpClient == null) {
					OkHttpClient.Builder builder = new OkHttpClient.Builder();
					builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
					builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
					builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
					SSLSocketFactory ssl = getSSLSocketFactory();
					if (ssl != null) {
						builder.sslSocketFactory(ssl);
					}
					if (HDRuntimeContext.get().isDebug()) {
						builder.addNetworkInterceptor(new StethoInterceptor());
						Interceptor chuckInterceptor = getHttpDebugInterceptor(HDRuntimeContext.get());
						if (chuckInterceptor != null) {
							builder.addInterceptor(chuckInterceptor);
						}
					}
					okHttpClient = builder.build();
				}
			}
		}
		return okHttpClient;
	}
	
	/**
	 * 下载文件专用的
	 *
	 * @param isRetry 是否启用从本地加载的拦截器
	 * @return
	 */
	static OkHttpClient getFileHttpClient(boolean isRetry) {
		if (isRetry) {
			return OkHttp3Wrapper.getFileCachePreferredClientInstance();
		}
		return OkHttp3Wrapper.getFileLastModifiedSupportClientInstance();
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static Interceptor getHttpDebugInterceptor(Context context) {
		try {
			Class chuckClass = Class.forName("com.readystatesoftware.chuck.ChuckInterceptor");
			//获取构造器,参数类型是String的Class类型
			Constructor con = chuckClass.getConstructor(Context.class);
			//利用构造器生成对象，并传入参数name
			return (Interceptor) con.newInstance(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static SSLSocketFactory getSSLSocketFactory() {
		SSLSocketFactory sslSocketFactory = null;
		InputStream stream = null;
		try {
			stream = HDRuntimeContext.get().getAssets().open("client_test.bks");
			// 创建 keyManger
			KeyStore clientKeyStore = KeyStore.getInstance("bks");
			clientKeyStore.load(stream, "123456".toCharArray());
			IOUtils.closeSilently(stream);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientKeyStore, "123456".toCharArray());
			
			// 创建 包含证书信息的 KeyStore
			stream = HDRuntimeContext.get().getAssets().open("server.cer");
			Certificate ca = CertificateFactory.getInstance("X.509").generateCertificate(stream);
			KeyStore trustedKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustedKeyStore.load(null);
			trustedKeyStore.setCertificateEntry("ca", ca);
			IOUtils.closeSilently(stream);
			
			// Create a TrustManager that trusts the CAs in our KeyStore
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustedKeyStore);
			
			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
			context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
			sslSocketFactory = context.getSocketFactory();
		} catch (Exception e) {
			Logger.t("Https").e(e, "getSSLSocketFactory");
		} finally {
			IOUtils.closeSilently(stream);
		}
		return sslSocketFactory;
	}

	public static Task<Object> doHttpsTest() {
		final String url = "https://";
		return Task.callInBackground(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				Logger.t("Https").d("start connect: %s", url);
				HDHttpRequest<String> request = new HDHttpRequest<>();
				request.setMethod(HDHttpRequest.GET);
				request.setUrl(url);
				return request.execute(new IParser.StringParser());
			}
		}).continueWith(new Continuation<Object, Object>() {
			@Override
			public Object then(Task<Object> task) throws Exception {
				if (task.isFaulted()) {
					task.getError().printStackTrace();
				} else {
					Logger.t("Https").d(task.getResult().toString());
				}
				return null;
			}
		});
	}
	
}
