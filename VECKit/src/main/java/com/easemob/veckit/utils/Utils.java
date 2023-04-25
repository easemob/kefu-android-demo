package com.easemob.veckit.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;

import com.easemob.veckit.R;
import com.easemob.veckit.bean.EnquiryOptionsBean;
import com.easemob.veckit.bean.OptionBean;
import com.easemob.veckit.ui.flow.FlowBean;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.helpdesk.util.Log;

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
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {
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

    private static void getDegreeFromTag(Map<Integer, ArrayList<FlowBean>> degreeBeanMap, JSONObject enquiryTags, String name) throws JSONException {
        ArrayList<FlowBean> arrayList = new ArrayList<>();
        if (enquiryTags.has(name)){
            JSONArray jsonArray = enquiryTags.getJSONArray(name);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject tag = (JSONObject) jsonArray.get(i);
                int id = tag.getInt("id");
                String tenantId = tag.getString("tenantId");
                int score = tag.getInt("score");
                String tagName = tag.getString("tagName");
                String createDateTime = tag.getString("createDateTime");
                String updateDateTime = tag.getString("updateDateTime");
                arrayList.add(new FlowBean(id, tenantId, score, tagName, createDateTime, updateDateTime));
            }
        }
        degreeBeanMap.put(Integer.valueOf(name), arrayList);
    }

    public static void getDegreeTag(Map<Integer, ArrayList<FlowBean>> degreeBeanMap, JSONObject jsonObject, int num){
        clearDegreeTag(degreeBeanMap);
        try {
            if (!jsonObject.has("enquiryTags")){
                return;
            }
            JSONObject enquiryTags = jsonObject.getJSONObject("enquiryTags");
            for (int i = 1; i <= num; i++){
                getDegreeFromTag(degreeBeanMap, enquiryTags, String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void clearDegreeTag(Map<Integer, ArrayList<FlowBean>> degreeBeanMap){
        if (degreeBeanMap == null){
            return;
        }
        for (ArrayList<FlowBean> arrayList : degreeBeanMap.values()){
            arrayList.clear();
        }
        degreeBeanMap.clear();
    }

    public static ArrayList<FlowBean> getDegreeTags(Map<Integer, ArrayList<FlowBean>> degreeBeanMap, int tag){
        if (degreeBeanMap == null){
            return new ArrayList<>();
        }

        if (tag > degreeBeanMap.size()){
            tag = degreeBeanMap.size();
        }

        if (tag < 0){
            tag = 0;
        }

        if (degreeBeanMap.containsKey(tag)){
            return degreeBeanMap.get(tag);
        }
        return new ArrayList<>();
    }

    public static String getText(Context context, int rating) {
        if (rating == 1){
            return Utils.getString(context, R.string.vec_star_one);
        }else if (rating == 2){
            return Utils.getString(context, R.string.vec_star_two);
        }else if (rating == 3){
            return Utils.getString(context, R.string.vec_star_three);
        }else if (rating == 4){
            return Utils.getString(context, R.string.vec_star_four);
        }else {
            return Utils.getString(context, R.string.vec_star_five);
        }
    }

    // 获取：文案设置
    // 邀请评价
    public static String getEnquiryInviteMsg(EnquiryOptionsBean enquiryOptionsBean){
        if (enquiryOptionsBean == null){
            return "";
        }
        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if ("EnquiryInviteMsg".equalsIgnoreCase(bean.optionName)){
                return bean.optionValue;
            }
        }

        return "";
    }

    // 获取：文案设置
    // 感谢评价
    public static String getEnquirySolveMsg(EnquiryOptionsBean enquiryOptionsBean){
        if (enquiryOptionsBean == null){
            return "";
        }
        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if ("EnquirySolveMsg".equalsIgnoreCase(bean.optionName)){
                return bean.optionValue;
            }
        }

        return "";
    }

    // 默认是否显示5星好评
    public static boolean getEnquiryDefaultShow5Score(EnquiryOptionsBean enquiryOptionsBean){
        if (enquiryOptionsBean == null){
            return false;
        }
        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if ("EnquiryDefaultShow5Score".equalsIgnoreCase(bean.optionName)){
                return "true".equalsIgnoreCase(bean.optionValue);
            }
        }

        return false;
    }

    // 默认是否显示评价备注
    public static boolean getEnquiryCommentEnable(EnquiryOptionsBean enquiryOptionsBean){
        if (enquiryOptionsBean == null){
            return false;
        }
        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if ("EnquiryCommentEnable".equalsIgnoreCase(bean.optionName)){
                return "true".equalsIgnoreCase(bean.optionValue);
            }
        }

        return false;
    }

    // 1星，2星，3星 必须填写备注
    public static boolean getDegreeEnquiryCommentEnable(EnquiryOptionsBean enquiryOptionsBean, int currentRating) {
        if (enquiryOptionsBean == null){
            return false;
        }

        String optionName = "";
        if (currentRating == 1){
            optionName = "EnquiryCommentFor1Score";
        }else if (currentRating == 2){
            optionName = "EnquiryCommentFor2Score";
        }else if (currentRating == 3){
            optionName = "EnquiryCommentFor3Score";
        }

        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if (optionName.equalsIgnoreCase(bean.optionName)){
                return "true".equalsIgnoreCase(bean.optionValue);
            }
        }

        return false;
    }

    // 1星，2星，3星 必须选择标签
    public static boolean getDegreeTagsEnable(EnquiryOptionsBean enquiryOptionsBean, int currentRating) {
        if (enquiryOptionsBean == null){
            return false;
        }

        String optionName = "";
        if (currentRating == 1){
            optionName = "EnquiryTagsFor1Score";
        }else if (currentRating == 2){
            optionName = "EnquiryTagsFor2Score";
        }else if (currentRating == 3){
            optionName = "EnquiryTagsFor3Score";
        }

        ArrayList<OptionBean> enquiryOptions = enquiryOptionsBean.enquiryOptions;
        for (OptionBean bean : enquiryOptions){
            if (optionName.equalsIgnoreCase(bean.optionName)){
                return "true".equalsIgnoreCase(bean.optionValue);
            }
        }

        return false;
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

    public static boolean isSupportScreenShare(){
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP;
    }
}
