package com.hyphenate.helpdesk.videokit.uitls;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.helpdesk.videokit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {

	private Utils() {
	}

	/*@SuppressLint("NewApi")
	public static void enableStrictMode() {
		if(Utils.hasGingerbread())
		{
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
					new StrictMode.ThreadPolicy.Builder()
							.detectAll()
							.penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder =
					new StrictMode.VmPolicy.Builder()
							.detectAll()
							.penaltyLog();

			if (Utils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				vmPolicyBuilder
						.setClassInstanceLimit(ImageGridActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;

	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= 19;
	}

	public static List<Size> getResolutionList(Camera camera)
	{
		Parameters parameters = camera.getParameters();
		return parameters.getSupportedPreviewSizes();
	}

	public static class ResolutionComparator implements Comparator<Size>{

		@Override
		public int compare(Size lhs, Size rhs) {
			if(lhs.height!=rhs.height)
				return lhs.height-rhs.height;
			else
				return lhs.width-rhs.width;
		}

	}*/


	private static Gson gson = new Gson();

	public static File getVecPath(Context context, String fileName){
		String path = context.getCacheDir().toString().concat("/vec");
		File search = new File(path);
		if (!search.exists()){
			//noinspection ResultOfMethodCallIgnored
			search.mkdirs();
		}
		return new File(path, fileName);
	}

	public static File saveImage(Context context, String fileName, Bitmap bitmap){
		File vecPath = getVecPath(context, fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(vecPath);
			if (bitmap != null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			} else {
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return vecPath;
	}

	public static int dp2px(Context context, float dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
	}

	public static int getThemePrimaryColor(Context context) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
		return typedValue.data;
	}

	public static boolean isDarkMode(Context context) {
		int nightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return nightMode == Configuration.UI_MODE_NIGHT_YES;
	}

	public static boolean isPhone(Context context) {
		return !isTablet(context);
	}

	public static boolean isTablet(Context context) {
		Configuration configuration = context.getResources().getConfiguration();
		return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}

	public static int getStateHeight(Context context) {
		int stateHeight = 0;
		Resources resources = context.getApplicationContext().getResources();
		int identifierState = resources.getIdentifier("status_bar_height", "dimen", "android");
		if (identifierState > 0) {
			stateHeight = resources.getDimensionPixelSize(identifierState);
		}
		if (stateHeight == 0){
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, resources.getDisplayMetrics());
		}

		return stateHeight;
	}

	public static String AES256Encode(String stringToEncode, String keyString)
			throws NullPointerException {
		if (TextUtils.isEmpty(keyString)) {
			return null;
		}
		if (TextUtils.isEmpty(stringToEncode)) {
			return null;
		}
		try {
			SecretKeySpec skeySpec = getKey(keyString);
			byte[] data = stringToEncode.getBytes("UTF8");
			final byte[] iv = new byte[16];
			Arrays.fill(iv, (byte) 0x00);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            /*String encrypedValue = Base64.encodeToString(cipher.doFinal(data),
                    Base64.DEFAULT);*/
			return bytes2Hex(cipher.doFinal(data));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("uuuuuuuuu","error = "+e.toString());
		}
		return null;
	}

	/**
	 *
	 * 说明 :AES256解密
	 *
	 * @param text
	 *            Base64格式密文
	 * @param keyString
	 *            密钥
	 * @return String格式明文
	 */
	public static String AES256Decrypt(String text, String keyString) {
		if (TextUtils.isEmpty(keyString)) {
			return null;
		}
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		try {
			SecretKey key = getKey(keyString);
			final byte[] iv = new byte[16];
			Arrays.fill(iv, (byte) 0x00);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			// byte[] data = Base64.decode(text, Base64.DEFAULT);
			byte[] data = toByte(text);
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			byte[] decrypedValueBytes = cipher.doFinal(data);
			return new String(decrypedValueBytes, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static SecretKeySpec getKey(String password)
			throws UnsupportedEncodingException {
		// 如果为128将长度改为128即可
		int keyLength = 256;
		byte[] keyBytes = new byte[keyLength / 8];
		// explicitly fill with zeros
		Arrays.fill(keyBytes, (byte) 0x0);
		byte[] passwordBytes = toByte(password);
		int length = Math.min(passwordBytes.length, keyBytes.length);
		System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
		return new SecretKeySpec(keyBytes, "AES");
	}

	/**
	 * byte数组转换为16进制字符串
	 *
	 * @param bts
	 *            数据源
	 * @return 16进制字符串
	 */
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	/**
	 * 将16进制转换为byte数组
	 *
	 * @param hexString
	 *            16进制字符串
	 * @return byte数组
	 */
	private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String getAction(String msgtype, String type) throws Exception{
		JSONObject msg = new JSONObject(msgtype);
		if (msg.has(type)){
			JSONObject microphone = msg.getJSONObject(type);
			if (microphone.has("action")){
				return microphone.getString("action");
			}
		}
		return null;
	}

	public static boolean isOn(String msgtype, String type)  throws Exception{
		return "on".equalsIgnoreCase(getAction(msgtype, type));
	}

	public static String getString(Context context, int stringId) {
		return context.getApplicationContext().getResources().getString(stringId);
	}
}
