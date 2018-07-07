package com.ctkj.xj_app.http;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * created by zhaohui on 2018/5/8 15:16
 */

public abstract class VolleyJSONObjectListener {


    public static Response.Listener<JSONObject> mListener;
    public static Response.ErrorListener mErrorListener;

    public VolleyJSONObjectListener(Response.Listener<JSONObject> listener,
                                    Response.ErrorListener errorListener) {
        mListener = listener;
        mErrorListener = errorListener;
    }

    public abstract void onMySuccess(JSONObject jsonObject);

    public abstract void onMyError(VolleyError error);

    public Response.Listener<JSONObject> responseListener() {
        mListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onMySuccess(response);
            }
        };
        return mListener;
    }

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
