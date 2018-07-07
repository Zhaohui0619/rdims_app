package com.hotdog.hdlibrary.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.os.Looper;
import android.support.annotation.StringRes;

import com.hotdog.hdlibrary.widget.HDProgressDialog;


public class HDProgress {
	private static Dialog dialog;

	public static void show(String text, Activity activity) {
		show(text, activity, true, null);
	}

	public static void show(@StringRes int resID, Activity activity) {
		if (activity == null) {
			return;
		}
		show(activity.getString(resID), activity, true, null);
	}

	public static void show(String text, Activity activity, boolean cancelable) {
		if (activity == null) {
			return;
		}
		show(text, activity, cancelable, null);
	}

	public static void show(final String text, final Activity activity, final boolean cancelAble, final OnCancelListener onCancelListener) {
		if (activity == null) {
			return;
		}
		if (Looper.getMainLooper() != Looper.myLooper()) {
			HDExecutors.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					show(text, activity, cancelAble, onCancelListener);
				}
			});
			return;
		}
		try {
			dismiss();
			dialog = HDProgressDialog.show(activity, text, cancelAble, onCancelListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dismiss() {
		if (Looper.getMainLooper() != Looper.myLooper()) {
			HDExecutors.runOnUIThread(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			});
			return;
		}
		try {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dialog = null;
	}

}
