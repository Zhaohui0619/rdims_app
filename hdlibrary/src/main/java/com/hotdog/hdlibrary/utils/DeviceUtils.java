package com.hotdog.hdlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Method;

public class DeviceUtils {

    private static final String TAG = "Device";

    public static void printDeviceInfo(Context context) {
        String pattern = "Name %s\nSDK version %s\nCPU number %s\n%s dpi %s";
        Object[] args = new Object[5];
        args[0] = Build.MODEL;
        args[1] = Build.VERSION.SDK_INT;
        args[2] = Runtime.getRuntime().availableProcessors();
        args[3] = context.getResources().getDisplayMetrics().toString();
        args[4] = context.getResources().getDisplayMetrics().densityDpi;
        Logger.t(TAG).d(String.format(pattern, args));
    }

	/**
	 * 获取手机识别�?
	 *
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String myAndroidDeviceId;
		if (tm.getDeviceId() != null) {
			myAndroidDeviceId = tm.getDeviceId();
		} else {
			myAndroidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		return myAndroidDeviceId;
	}


	public static int getWidthPixels(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getHeightPixels(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	//获取屏幕原始尺寸高度，包括虚拟功能键高度
	public static int getDpi(Context context) {
		int dpi = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, displayMetrics);
			dpi = displayMetrics.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dpi;
	}

	/**
	 * 获取 虚拟按键的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getBottomStatusHeight(Context context) {
		int totalHeight = getDpi(context);
		int contentHeight = getScreenHeight(context);
		return totalHeight - contentHeight;
	}

	/**
	 * 标题栏高度
	 *
	 * @return
	 */
	public static int getTitleHeight(Activity activity) {
		return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	}

}
