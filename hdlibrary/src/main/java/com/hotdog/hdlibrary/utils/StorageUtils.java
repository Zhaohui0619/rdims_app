package com.hotdog.hdlibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

public class StorageUtils {

	/**
	 * 临时目录文件存放时间,15天
	 */
	public static final long TEMP_FILE_EXISTS_TIME = 1000 * 60 * 60 * 24 * 15;

	private static final String TAG  = StorageUtils.class.getSimpleName();
	private static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

	private static final String TEMP  = "temp";
	private static final String VOICE = "voice";
	private static final String IMAGE = "image";
	private static final String VIDEO = "video";
	private static final String DATA  = "data";
	private static final String FILE  = "file";
	private static final String CACHE = "cache";
	private static final String DOWNLOAD  = "download";

	//最小存储空间
	private static final int MIN_STORAGE_SIZE = 1024 * 1024 * 100;


	/**
	 * 获取储存的根目录,所有程序自建的文件(不包括第三方框架管理的)都放下这个目录下面
	 *
	 * @return
	 */
	public static String getRootDirectory(Context context) {
		File file = new File(ROOT, context.getPackageName());
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getStorageDirectory(Context context, @Nullable String dirName) {
		final String rootPath = getRootDirectory(context);
		if (TextUtils.isEmpty(dirName)) {
			return rootPath;
		}
		File file = new File(rootPath, dirName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getTempDirectory(Context context) {
		return getStorageDirectory(context, TEMP);
	}

	public static String getVoiceDirectory(Context context) {
		return getStorageDirectory(context, VOICE);
	}

	public static String getImageDirectory(Context context) {
		return getStorageDirectory(context, IMAGE);
	}

	public static String getVideoDirectory(Context context) {
		return getStorageDirectory(context, VIDEO);
	}

	public static String getDownloadDirectory(Context context) {
		return getStorageDirectory(context, DOWNLOAD);
	}

	public static String getDataDirectory(Context context) {
		return getStorageDirectory(context, DATA);
	}

	public static String getCacheDirectory(Context context) {
		return getStorageDirectory(context, CACHE);
	}

	public static String getFileDirectory(Context context) {
		return getStorageDirectory(context, FILE);
	}

	/**
	 * 判断是否有足够的存储空间
	 * @return
     */
	public static boolean isStorageEnough() {
		File sdcard_dir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
		long usableSpace = sdcard_dir.getUsableSpace();//获取文件目录对象剩余空间
		return usableSpace < MIN_STORAGE_SIZE;
	}
	
	public static String getFilePath(String dirName, String fileName){
		return dirName + File.separator + fileName;
	}
}
