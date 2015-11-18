package com.easemob.helpdeskdemo.utils;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class CommonUtils {
	public static Bitmap convertBitmap(Bitmap oldBitmap, int reqWidth, int reqHeight) {
		// 获取图片的宽高
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();

		float scaleWidth = 0;
		float scaleHeight = 0;

		if (width < height) {
			if (height < reqHeight) {
				int newHeight = reqHeight;
				float newWidth = width * (((float) reqHeight) / height);
				// 计算缩放比例
				scaleWidth = ((float) newWidth) / width + 1;
				scaleHeight = ((float) newHeight) / height + 1;
			} else {
				// 设置想要的大小
				int newWidth = reqWidth;
				float newHeight = height * (((float) reqWidth) / width);
				// 计算缩放比例
				scaleWidth = ((float) newWidth) / width;
				scaleHeight = ((float) newHeight) / height;
			}
		} else {
			if (width < reqWidth) {
				// 设置想要的大小
				int newWidth = reqWidth;
				float newHeight = height * (((float) reqWidth) / width);
				// 计算缩放比例
				scaleWidth = ((float) newWidth) / width + 1;
				scaleHeight = ((float) newHeight) / height + 1;
			} else {
				// 设置想要的大小
				int newHeight = reqHeight;
				float newWidth = width * (((float) reqHeight) / height);
				// 计算缩放比例
				scaleWidth = ((float) newWidth) / width;
				scaleHeight = ((float) newHeight) / height;
			}
		}
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
		return newbm;
	}

	// 转换dip为px
	public static int convertDip2Px(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	// 转换px为dip
	public static int convertPx2Dip(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
	}

	public static String getRandomAccount() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) // 字符串
			{
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) // 数字
			{
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val.toLowerCase();
	}
}
