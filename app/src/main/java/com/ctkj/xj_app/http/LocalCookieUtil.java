package com.ctkj.xj_app.http;

/**
 * created by zhaohui on 2018/5/8 15:16
 */

public class LocalCookieUtil {

    private static String localCookie = null;


    public static void setLocalCookie(String localCookie) {
        LocalCookieUtil.localCookie = localCookie;
    }

    public static String getLocalCookie() {
        return localCookie;
    }
}
