package com.hotdog.hdlibrary.core;

import android.content.Context;
import android.content.SharedPreferences;

public class HDSharePreference {

	public static final String USERID = "userId";

	private static final String NAME = "properties";
	
	public static SharedPreferences get(Context context, String userId, String key) {
		return context.getSharedPreferences(userId + "_" + key, Context.MODE_PRIVATE);
	}

	private static SharedPreferences get(Context context, String userId) {
		return context.getSharedPreferences(NAME + userId, Context.MODE_PRIVATE);
	}

	public static boolean put(Context context, String key, long value) {
		return get(context, "").edit().putLong(key, value).commit();
	}

	public static boolean put(Context context, String userId, String key, long value) {
		return get(context, userId).edit().putLong(key, value).commit();
	}

	public static boolean put(Context context, String key, String value) {
		return get(context, "").edit().putString(key, value).commit();
	}

	public static boolean put(Context context, String userId, String key, String value) {
		return get(context, userId).edit().putString(key, value).commit();
	}

	public static boolean put(Context context, String key, int value) {
		return get(context, "").edit().putInt(key, value).commit();
	}

	public static boolean put(Context context, String userId, String key, int value) {
		return get(context, userId).edit().putInt(key, value).commit();
	}

	public static boolean put(Context context, String key, boolean value) {
		return get(context, "").edit().putBoolean(key, value).commit();
	}

	public static boolean put(Context context, String userId, String key, boolean value) {
		return get(context, userId).edit().putBoolean(key, value).commit();
	}

	public static long getLong(Context context, String key, long defValue) {
		return get(context, "").getLong(key, defValue);
	}

	public static long getLong(Context context, String userId, String key, long defValue) {
		return get(context, userId).getLong(key, defValue);
	}
	
	public static long getLong(Context context, String userId, String spKey, String valueKey, long defValue) {
		return get(context, userId, spKey).getLong(valueKey, defValue);
	}

	public static String getString(Context context, String key, String defValue) {
		return get(context, "").getString(key, defValue);
	}

	public static String getString(Context context, String userId, String key, String defValue) {
		return get(context, userId).getString(key, defValue);
	}

	public static int getInt(Context context, String key, int defValue) {
		return get(context, "").getInt(key, defValue);
	}

	public static int getInt(Context context, String userId, String key, int defValue) {
		return get(context, userId).getInt(key, defValue);
	}
	
	public static int getInt(Context context, String userId, String spKey, String valueKey, int defValue) {
		return get(context, userId, spKey).getInt(valueKey, defValue);
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		return get(context, "").getBoolean(key, defValue);
	}

	public static boolean getBoolean(Context context, String userId, String key, boolean defValue) {
		return get(context, userId).getBoolean(key, defValue);
	}

	public static boolean remove(Context context, String key) {
		return get(context, "").edit().remove(key).commit();
	}

	public static boolean remove(Context context, String userId, String key) {
		return get(context, "").edit().remove(key).commit();
	}

}
