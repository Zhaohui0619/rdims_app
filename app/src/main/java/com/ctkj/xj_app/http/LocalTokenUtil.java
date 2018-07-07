package com.ctkj.xj_app.http;

/**
 * created by zhaohui on 2018/5/8 15:16
 */

public class LocalTokenUtil {
    private static String localToken = null;


    public static void setlocalToken(String localToken) {
        LocalTokenUtil.localToken = localToken;
    }

    public static String getlocalToken() {
        return localToken;
    }
}
