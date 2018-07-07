package com.hotdog.hdlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.hotdog.hdlibrary.core.HDException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class BitmapUtils {

	private static final String TAG = "BitmapUtil";

	/**
	 * @param data
	 * @return bitmap
	 */
	public static Bitmap base64ToBitmap(String data) {
		return base64StrToBitmap(data, Base64.DEFAULT);
	}

	/**
	 * @param data
	 * @return bitmap
	 */
	public static Bitmap base64StrToBitmap(String data, int base64Flag) {
		byte[] bytes = Base64.decode(data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	@Nullable
	public static String image2Base64(String imageFilePath, int imageSize) throws HDException {
		if (TextUtils.isEmpty(imageFilePath)) {
			return null;
		}
		try {
			Bitmap bmp = compressBitmap(imageFilePath, imageSize);
			if (bmp == null) {
				return "";
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 80, output);
			bmp.recycle();

			byte[] result = output.toByteArray();
			IOUtils.closeSilently(output);
			return Base64.encodeToString(result, Base64.DEFAULT);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new HDException(e);
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		IOUtils.closeSilently(output);
		return result;
	}

	public static Bitmap readBitmap(String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		FileInputStream is;
		try {
			is = new FileInputStream(path);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			if (bitmap.getWidth() > 500 && bitmap.getHeight() > 500){
				return compressBitmap(path, 500);
			}
			return bitmap;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap decodeSampledBitmapFromFile(String filePath) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 640, 640);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static Bitmap compressBitmap(String filePath, int imageSize) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		if (options.outHeight > imageSize || options.outWidth > imageSize) {
			// Calculate inSampleSize
			options.inSampleSize = getinSampleSize(options, imageSize);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(filePath, options);
		}
		else {
			return BitmapFactory.decodeFile(filePath, null);
		}
	}

	public static int getinSampleSize(BitmapFactory.Options options, int imageSize) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		float reqHeight = imageSize;
		float reqWidth = imageSize;
		int inSampleSize = 1;

		if (height >= width && height > reqHeight) {
			inSampleSize = (int) Math.round(height / reqHeight);
//			inSampleSize = (int) Math.round(inSampleSize * 1.5);
		}
		else if (width > height && width > reqWidth) {
			inSampleSize = (int) Math.round(width / reqWidth);
//			inSampleSize = (int) Math.round(inSampleSize * 1.5);
		}

		return inSampleSize;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	/**
	 * 获取视频的缩略图 先�?过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图�?
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的�?，这样会节省内存�?
	 *
	 * @param videoPath 视频的路�?
	 * @param width     指定输出视频缩略图的宽度
	 * @param height    指定输出视频缩略图的高度�?
	 * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND�?
	 *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		System.out.println("w" + bitmap.getWidth());
		System.out.println("h" + bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 读取图片属�?：旋转的角度
	 *
	 * @param path 图片绝对路径
	 * @return degree旋转的角�?
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static byte[] imageFileToUploadByteArray(String filePath, int imageSize) {
		Bitmap bitmap = BitmapUtils.compressBitmap(filePath, imageSize);
		Bitmap resizedBitmap = getResizedBitmap(filePath, bitmap);
		return bmpToByteArray(resizedBitmap, true);
	}

	public static Bitmap getResizedBitmap(String filePath, Bitmap bitmap) {
		int degree = BitmapUtils.readPictureDegree(filePath);
		Bitmap resizedBitmap = null;
		if (degree == 0) {
			resizedBitmap = bitmap;
		}
		else {
			// 旋转图片
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			// 创建新的图片
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			if (bitmap != resizedBitmap) {
				bitmap.recycle();
			}
		}
		return resizedBitmap;
	}


	public static String bitmap2File(Bitmap bm, String filePath) {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
			bm.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		finally {
			IOUtils.closeSilently(bos);
		}
		return filePath;
	}

	public static String bitmap2Base64Str(Bitmap bitmap, int base64Flag) {
		ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
		CompressFormat compressFormat = CompressFormat.JPEG;
		try {
			if (bitmap.compress(compressFormat, 80, jpeg_data)) {
				byte[] code = jpeg_data.toByteArray();
				byte[] output = Base64.encode(code, base64Flag);
				return new String(output);
			}
			throw new RuntimeException("bitmap can't be compressed");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
