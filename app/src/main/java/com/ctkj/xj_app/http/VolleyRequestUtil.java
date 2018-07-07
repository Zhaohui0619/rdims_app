package com.ctkj.xj_app.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ctkj.xj_app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by zhaohui on 2018/5/8 15:17
 */

public class VolleyRequestUtil {

    private static StringRequest stringRequest;
    private static int sTimeOut = 30000;
    private static Map<String, String> sendHeader = new HashMap<>();

    /**
     * 返回String 类型数据的get请求
     *
     * @param url                  string请求url
     * @param tag                  请求的标志
     * @param volleyStringListener 监听接口
     * @param timeOutDefaultFlag   请求超时设置标志
     */
    public static void stringRequestGet(String url, String tag,
                                 VolleyStringListener volleyStringListener,
                                 boolean timeOutDefaultFlag) {
        //清除请求队列中的tag标记请求
        MyApplication.getHttpQueue().cancelAll(tag);
        //创建当前的请求，获取字符串内容
        stringRequest = new StringRequest(Request.Method.GET, url,
                volleyStringListener.responseListener(), volleyStringListener.errorListener());
        //为当前请求添加标记
        stringRequest.setTag(tag);
        //默认超时时间以及重连次数
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //将当前请求添加到请求队列中
        MyApplication.getHttpQueue().add(stringRequest);
        //重启当前请求队列
        MyApplication.getHttpQueue().start();

    }

    /**
     * 返回String 类型数据的post请求
     *
     * @param url                  string请求url
     * @param tag                  请求标志
     * @param params               post请求携带的数据
     * @param volleyStringListener 监听接口
     * @param timeOutDefaultFlag   请求超时设置标志
     */
    public static void stringRequestPost(String url, String tag,
                                  final Map<String, String> params,
                                  VolleyStringListener volleyStringListener,
                                  boolean timeOutDefaultFlag) {
        MyApplication.getHttpQueue().cancelAll(tag);
        stringRequest = new StringRequest(Request.Method.POST, url,
                volleyStringListener.responseListener(), volleyStringListener.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(stringRequest);
        MyApplication.getHttpQueue().start();

    }

    /**
     * 返回jsonObject的get请求  第一次
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */

    public static void jsonObjectGetRequest(String url, String tag,
                                            final VolleyJSONObjectListener volleyJSONObjectListener,
                                            boolean timeOutDefaultFlag) {
        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                volleyJSONObjectListener.responseListener(), volleyJSONObjectListener.errorListener()) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String cookieFromResponse;
                    String jsonString =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    String mHeader = response.headers.toString();
                    Log.w("LOG", "get headers in parseNetworkResponse " + response.headers.toString());
                    //使用正则表达式从response的头中提取cookie内容的子串
                    Pattern pattern = Pattern.compile("Set-Cookie.*?;");
                    Matcher m = pattern.matcher(mHeader);
                    if (m.find()) {
                        cookieFromResponse = m.group();
                        Log.w("LOG", "cookie from server " + cookieFromResponse);
                        //去掉cookie末尾的分号
                        cookieFromResponse = cookieFromResponse.substring(11, cookieFromResponse.length() - 1);
                        Log.w("LOG", "cookie substring " + cookieFromResponse);
                        LocalCookieUtil.setLocalCookie(cookieFromResponse);
                        //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("Cookie", cookieFromResponse);
                        Log.w("LOG", "jsonObject " + jsonObject.toString());
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("Cookie", LocalCookieUtil.getLocalCookie());
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));
                    }
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }
        };
        jsonObjectRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (LocalCookieUtil.getLocalCookie() != null) {
            setSendCookie(LocalCookieUtil.getLocalCookie());
        }
        MyApplication.getHttpQueue().add(jsonObjectRequest);
        MyApplication.getHttpQueue().start();
    }

    /**
     * 返回jsonObject的get请求
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */
    public static void jsonObjectRequestGet(String url, String tag,
                                            final VolleyJSONObjectListener volleyJSONObjectListener,
                                            boolean timeOutDefaultFlag) {

        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                volleyJSONObjectListener.responseListener(), volleyJSONObjectListener.errorListener()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }
        };
        jsonObjectRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (LocalCookieUtil.getLocalCookie() != null) {
            setSendCookie(LocalCookieUtil.getLocalCookie());
        }
        MyApplication.getHttpQueue().add(jsonObjectRequest);
        MyApplication.getHttpQueue().start();
    }

    /**
     *返回jsonObject、携带Map<String,String>的post请求  第一次
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param map                      post请求携带的数据
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */
    public static void jsonObjectPostMapRequest(String url, String tag,
                                                final Map<String ,String> map,
                                                final VolleyJSONObjectListener volleyJSONObjectListener,
                                                boolean timeOutDefaultFlag){
        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(Request.Method.POST,url,
                volleyJSONObjectListener.responseListener(),volleyJSONObjectListener.errorListener(),map){

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String cookieFromResponse;
                    String jsonString =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    String mHeader = response.headers.toString();
                    Log.w("LOG", "get headers in parseNetworkResponse " +mHeader);
                    //使用正则表达式从reponse的头中提取cookie内容的子串
                    Pattern pattern = Pattern.compile("Set-Cookie.*?;");
                    Matcher m = pattern.matcher(mHeader);
                    if (m.find()) {
                        Log.w("LOG", "find cookie" );
                        cookieFromResponse = m.group();
                        Log.w("LOG", "cookie from server " + cookieFromResponse);
                        //去掉cookie末尾的分号
                        cookieFromResponse = cookieFromResponse.substring(11, cookieFromResponse.length() - 1);
                        Log.w("LOG", "cookie substring " + cookieFromResponse);
                        //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("Cookie", cookieFromResponse);
                        Log.w("LOG", "jsonObject " + jsonObject.toString());
                        LocalCookieUtil.setLocalCookie(cookieFromResponse);
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        Log.w("LOG", "not find cookie" );
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("cookie", LocalCookieUtil.getLocalCookie());
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));

                    }
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }

        };

        jsonObjectPostRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectPostRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getHttpQueue().add(jsonObjectPostRequest);
        MyApplication.getHttpQueue().start();
    }


    /**
     * 返回jsonObject、携带Map<String,String>的post请求
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param map                      post请求携带的数据
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */
    public static void jsonObjectRequestPostMap(String url, String tag,
                                                final Map<String,String> map,
                                                final VolleyJSONObjectListener volleyJSONObjectListener,
                                                boolean timeOutDefaultFlag) {
        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(Request.Method.POST,url,
                volleyJSONObjectListener.responseListener(),volleyJSONObjectListener.errorListener(),map){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }
        };
        jsonObjectPostRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectPostRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (LocalCookieUtil.getLocalCookie() != null) {
            setSendCookie(LocalCookieUtil.getLocalCookie());
        }
        MyApplication.getHttpQueue().add(jsonObjectPostRequest);
        MyApplication.getHttpQueue().start();
    }

    /**
     * 返回jsonObject、携带jsonObject的post请求  第一次
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param jsonObject               post请求携带的json数据
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */
    public static void jsonObjectPostJsonRequest(String url, String tag,
                                                 final JSONObject jsonObject,
                                                 final VolleyJSONObjectListener volleyJSONObjectListener,
                                                 boolean timeOutDefaultFlag) {
        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                volleyJSONObjectListener.responseListener(), volleyJSONObjectListener.errorListener()) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String cookieFromResponse;
                    String jsonString =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    String mHeader = response.headers.toString();
                    Log.w("LOG", "get headers in parseNetworkResponse " + response.headers.toString());
                    //使用正则表达式从reponse的头中提取cookie内容的子串
                    Pattern pattern = Pattern.compile("Set-Cookie.*?;");
                    Matcher m = pattern.matcher(mHeader);
                    if (m.find()) {
                        cookieFromResponse = m.group();
                        Log.w("LOG", "cookie from server " + cookieFromResponse);
                        //去掉cookie末尾的分号
                        cookieFromResponse = cookieFromResponse.substring(11, cookieFromResponse.length() - 1);
                        Log.w("LOG", "cookie substring " + cookieFromResponse);
                        //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("Cookie", cookieFromResponse);
                        Log.w("LOG", "jsonObject " + jsonObject.toString());
                        LocalCookieUtil.setLocalCookie(cookieFromResponse);
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));
                    } else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        jsonObject.put("Cookie", LocalCookieUtil.getLocalCookie());
                        return Response.success(jsonObject,
                                HttpHeaderParser.parseCacheHeaders(response));

                    }
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }
        };
        jsonObjectRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (LocalCookieUtil.getLocalCookie() != null) {
            setSendCookie(LocalCookieUtil.getLocalCookie());
        }
        MyApplication.getHttpQueue().add(jsonObjectRequest);
        MyApplication.getHttpQueue().start();


    }

    /**
     * 返回jsonObject、携带jsonObject的post请求
     *
     * @param url                      json请求url
     * @param tag                      请求标志
     * @param jsonObject               post请求携带的json数据
     * @param volleyJSONObjectListener 监听接口
     * @param timeOutDefaultFlag       请求超时设置标志
     */
    public static void jsonObjectRequestPostJson(String url, String tag,
                                                 final JSONObject jsonObject,
                                                 final VolleyJSONObjectListener volleyJSONObjectListener,
                                                 boolean timeOutDefaultFlag) {
        MyApplication.getHttpQueue().cancelAll(tag);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                volleyJSONObjectListener.responseListener(), volleyJSONObjectListener.errorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return sendHeader;
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                volleyJSONObjectListener.responseListener().onResponse(response);
            }
        };
        jsonObjectRequest.setTag(tag);
        int myTimeOut = timeOutDefaultFlag ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : sTimeOut;
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(myTimeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (LocalCookieUtil.getLocalCookie() != null) {
            setSendCookie(LocalCookieUtil.getLocalCookie());
        }
        MyApplication.getHttpQueue().add(jsonObjectRequest);
        MyApplication.getHttpQueue().start();
    }



    /**
     * @param cookie 放入发送请求头部信息的cookie
     */
    private static void setSendCookie(String cookie) {
        sendHeader.put("cookie", cookie);
    }

    /**
     * @param token 放入发送请求头部信息的token
     */
    private static void setSendToken(String token) {
        sendHeader.put("token", token);
    }

}
