package com.hotdog.hdlibrary.core;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;

public class HDException extends RuntimeException {

	private static final long serialVersionUID = -1438496563715140162L;

	private static final String TAG = HDException.class.getSimpleName();

	private final int code;
	private final String message;

	public HDException(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public HDException(HDError type) {
		super();
		this.code = type.getCode();
		this.message = type.getInfo();
	}

	public HDException(Throwable src) {
		super();
		if (src instanceof HDException) {
			this.code = ((HDException) src).getCode();
			this.message = src.getMessage();
		}
		else if (src instanceof ConnectException){
			this.code = HDError.ERROR_TIMEOUT.getCode();
			this.message = HDError.ERROR_TIMEOUT.getInfo();
		}
		else if (src instanceof JSONException) {
			this.code = HDError.ERROR_JSON.getCode();
			this.message = HDError.ERROR_JSON.getInfo();
		}
		else if (src instanceof IOException) {
			this.code = HDError.IO_EXCEPTION.getCode();
			this.message = HDError.IO_EXCEPTION.getInfo();
		}
		else {
			this.code = HDError.UNKONWN.getCode();
			this.message = HDError.UNKONWN.getInfo();
		}
	}

	@Override
	public String getMessage() {
		return message;// 从配置文件里面拿
	}

	public int getCode() {
		return code;
	}

	public HDError getCustomError() {
		return HDError.valueOf(code);
	}
}
