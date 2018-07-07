package com.hotdog.hdlibrary.core;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 单例Toast
 * 如果第一个Toast还没显示完，则第二个Toast取代第一个，重新显示
 */
public class HDToast {
	private static Toast mToast = null;

	public static void show(final String text, final Context context) {
		if (!TextUtils.isEmpty(text) && context != null) {
			showToast(context, text, Toast.LENGTH_LONG);
		}
	}

	public static void show(final int resId, final Context context) {
		if (context != null) {
			showToast(context, resId, Toast.LENGTH_LONG);
		}
	}

	public static void showLong(final int resId, final Context context) {
		if (context != null) {
			showToast(context, resId, Toast.LENGTH_LONG);
		}
	}

	public static void showException(final HDException exCode, final Context context) {
		if (context != null) {
			showToast(context, exCode.getMessage(), Toast.LENGTH_SHORT);
		}
	}

	/**
	 *  Toast提示，只显示一个
	 * @param context
	 * @param msg 内容，可以为int或者String类型
	 * @param length 显示时间长短
	 */
	private static void showToast(Context context, Object msg, int length) {
		if (null == mToast) {
			if (msg instanceof Integer) {
				mToast = Toast.makeText(context, (Integer) msg, length);
			} else {
				mToast = Toast.makeText(context, (String) msg, length);
			}
		} else {
			if (msg instanceof Integer) {
				mToast.setText((Integer) msg);
			} else {
				mToast.setText((String) msg);
			}
		}
		mToast.setDuration(length);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}
}
