package com.hotdog.hdlibrary.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.widget.TextView;

import com.hotdog.hdlibrary.R;

public class HDProgressDialog {

	public static Dialog show(final Activity context, String msg, boolean cancelAble, OnCancelListener onCancelListener) {
		if (context == null || context.isFinishing()) {
			return null;
		}
		Dialog dialog = new Dialog(context);
		dialog.setOwnerActivity(context);
		dialog.setCancelable(cancelAble);
		dialog.setCanceledOnTouchOutside(cancelAble);
		if (onCancelListener != null) {
			dialog.setOnCancelListener(onCancelListener);
		}
		View view = View.inflate(context, R.layout.layout_dialog_loading, null);
		TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
		tipTextView.setText(msg);// 设置加载信息
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}

}
