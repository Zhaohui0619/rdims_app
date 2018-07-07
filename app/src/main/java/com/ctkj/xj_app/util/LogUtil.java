package com.ctkj.xj_app.util;

import android.util.Log;

/**
 * created by zhaohui on 2018/6/6 9:55
 */


/**
 * 日志工具
 * 让 level 等于 VERBOSE 就可以把所有的日志都打印出来
 * 让 level 等于 NOTHING 就可以吧所有日志都屏蔽掉
 * <p>
 * LogUtil.d("TAG","debug log")即可调用
 */
public class LogUtil {
    private static final Integer VERBOSE = 1;

    private static final Integer DEBUG = 2;

    private static final Integer INFO = 3;

    private static final Integer WARN = 4;

    private static final Integer ERROR = 5;

    private static final Integer NOTHING = 6;

    private static int level = VERBOSE;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level < ERROR) {
            Log.e(tag, msg);
        }
    }
}
