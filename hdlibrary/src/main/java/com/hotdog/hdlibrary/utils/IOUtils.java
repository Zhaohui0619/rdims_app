package com.hotdog.hdlibrary.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 流的常用操作以及一些常用的输入输出转换
 */

public class IOUtils {

	public static final int COPY_BUF_SIZE = 1024 * 8;

	/**
	 * Copies the content of a InputStream into an OutputStream.
	 * Uses a default buffer size of 8024 bytes.
	 *
	 * @param input  the InputStream to copy
	 * @param output the target Stream
	 * @return the number of bytes copied
	 * @throws IOException if an error occurs
	 */
	public static long copy(final InputStream input, final OutputStream output) throws IOException {
		return copy(input, output, false);
	}

	/**
	 * Copies the content of a InputStream into an OutputStream
	 *
	 * @param input      the InputStream to copy
	 * @param output     the target Stream
	 * @return the number of bytes copied
	 * @throws IOException if an error occurs
	 */
	public static long copy(final InputStream input, final OutputStream output, boolean closeOnEnd) throws IOException {
		try {
			final byte[] buffer = new byte[COPY_BUF_SIZE];
			int n = 0;
			long count = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
				count += n;
			}
			return count;
		} finally {
			if (closeOnEnd) {
				closeSilently(input);
				closeSilently(output);
			}
		}
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a
	 * <code>Writer</code> using the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * This method uses {@link InputStreamReader}.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output  the <code>Writer</code> to write to
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to chars on a
	 * <code>Writer</code> using the specified character encoding.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method uses {@link InputStreamReader}.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param output  the <code>Writer</code> to write to
	 * @param encoding  the encoding to use, null means platform default
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 * @since Commons IO 1.1
	 */
	public static void copy(InputStream input, Writer output, String encoding) throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(in, output);
		}
	}

	/**
	 * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * <p>
	 * Large streams (over 2GB) will return a chars copied value of
	 * <code>-1</code> after the copy has completed since the correct
	 * number of chars cannot be returned as an int. For large streams
	 * use the <code>copyLarge(Reader, Writer)</code> method.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output  the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 * @throws ArithmeticException if the character count is too large
	 * @since Commons IO 1.1
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 *
	 * @param input  the <code>Reader</code> to read from
	 * @param output  the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 * @since Commons IO 1.3
	 */
	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[COPY_BUF_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * @param closeable 关闭流,忽略异常
	 */
	public static void closeSilently(@Nullable Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			}
			catch (IOException ignored) {

			}
		}
	}

	/**
	 * 流转 utf-8编码 string
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String stream2String(InputStream in) throws IOException {
		return toString(in);
	}

	/**
	 * 读取流内容
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] stream2Bytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			copy(in, out);
			return out.toByteArray();
		} finally {
			IOUtils.closeSilently(in);
			IOUtils.closeSilently(out);
		}
	}

	public static String bytes2HexStr(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String t = Integer.toHexString(0xFF & b);
			if (t.length() == 1) {
				hexString.append("0").append(t);
			} else {
				hexString.append(t);
			}
		}
		return hexString.toString();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String
	 * using the default character encoding of the platform.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws IOException if an I/O error occurs
	 */
	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a String
	 * using the specified character encoding.
	 * <p>
	 * Character encoding names can be found at
	 * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedInputStream</code>.
	 *
	 * @param input  the <code>InputStream</code> to read from
	 * @param encoding  the encoding to use, null means platform default
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws IOException if an I/O error occurs
	 */
	public static String toString(InputStream input, String encoding) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding);
		return sw.toString();
	}


	@Nullable
	public static String localUri2Path(Context context, Uri imageUri) {
		if (context == null || imageUri == null) {
			return null;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[]{split[1]};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri)) {
				return imageUri.getLastPathSegment();
			} else {
				return Environment.getExternalStorageDirectory() + imageUri.toString().split("external_files")[1];
			}
//			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		}
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}


}
