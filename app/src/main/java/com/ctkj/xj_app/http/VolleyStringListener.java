package com.ctkj.xj_app.http;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * created by zhaohui on 2018/5/8 15:37
 */

public abstract class VolleyStringListener {

    private static final String TAG = "Volley StringResponse";

    public static Response.Listener<String> mListener;
    public static Response.ErrorListener mErrorListener;

    public VolleyStringListener(Response.Listener<String> listener,
                                Response.ErrorListener errorListener) {
        mListener = listener;
        mErrorListener = errorListener;
    }


    //请求成功时的回调函数
    public abstract void onMySuccess(String response);

    //请求失败时的回调函数
    public abstract void onMyError(VolleyError error);

    //创建请求成功的事件监听
    public Response.Listener<String> responseListener() {
        mListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onMySuccess(response);
            }
        };
        return mListener;
    }


    //创建请求失败的事件监听
    public Response.ErrorListener errorListener() {
        mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onMyError(error);
            }
        };
        return mErrorListener;
    }

}
