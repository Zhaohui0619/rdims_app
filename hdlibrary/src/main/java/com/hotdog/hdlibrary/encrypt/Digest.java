package com.hotdog.hdlibrary.encrypt;

import android.support.annotation.Nullable;

import com.hotdog.hdlibrary.utils.FileUtils;
import com.hotdog.hdlibrary.utils.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Digest {

	/**
	 * @param data 数据
	 * @return 结果
	 */
	public static long crc32(@Nullable byte[] data) {
		if (data == null) {
			return 0;
		}
		CRC32 crc32 = new CRC32();
		crc32.update(data);
		return crc32.getValue();
	}

	/**
	 * 计算文件的crc32值
	 *
	 * @param file 待计算的文件
	 * @return 结果
	 */
	public static long fileCRC32(@Nullable File file) {
		try {
			return FileUtils.checksumCRC32(file);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * md5算法
	 *
	 * @param data 数据
	 * @return
	 */
	@Nullable
	public static String md5Hex(@Nullable byte[] data) {
		if (data == null) {
			return null;
		}
		return md5Hex(new ByteArrayInputStream(data), true);
	}

	/**
	 * md5算法
	 *
	 * @param file 数据
	 * @return
	 */
	@Nullable
	public static String fileMD5Hex(@Nullable File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		try {
			return md5Hex(new FileInputStream(file), true);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * md5算法
	 *
	 * @param inputStream 数据
	 * @return
	 */
	@Nullable
	public static String md5Hex(@Nullable InputStream inputStream, boolean closedOnEnd) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[IOUtils.COPY_BUF_SIZE];
			int read;
			while ((read = inputStream.read(buffer)) != -1) {
				md5.update(buffer, 0, read);
			}
			byte[] messageDigest = md5.digest();
			return IOUtils.bytes2HexStr(messageDigest);
		}
		catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		finally {
			if (closedOnEnd) {
				IOUtils.closeSilently(inputStream);
			}
		}
		return null;
	}


}
