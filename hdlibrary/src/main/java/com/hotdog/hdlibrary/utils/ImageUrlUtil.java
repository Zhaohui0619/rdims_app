package com.hotdog.hdlibrary.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

public class ImageUrlUtil {
	public static final String STORAGE = "storage";
	public static final String SYSTEM = "system";

	private static final int FUNDUS_WIDTH = 264;//压缩后眼底照片长
	private static final int FUNDUS_HEIGHT = 150;//压缩后眼底照片高

	public enum ImageSize {
		ORIGINAL(0),
		AVATAR(200);

		private int value = 0;

		ImageSize(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	/**
	 * 判断某个路径是否是本地的路径
	 *
	 * @param path
	 * @return
	 */
	public static boolean isLocalPath(String path) {
		return !TextUtils.isEmpty(path) && (path.contains(SYSTEM) || path.contains(STORAGE));
	}

	public static boolean isLocalPath1(String path) {
		return (path != null) && (path.contains(SYSTEM) || path.contains(STORAGE));
	}

	public static String[] splitImages(String images) {
		if (images != null && !images.isEmpty()) {
			return images.split(";");
		}
		return new String[0];
	}

	public static String[] splitImages(JSONArray imagesArray) throws JSONException {
		String[] images = new String[imagesArray.length()];
		for (int i = 0; i < imagesArray.length(); i++) {
			images[i] = imagesArray.get(i) + ".jpg";
		}
		return images;
	}

}
