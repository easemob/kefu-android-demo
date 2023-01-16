package com.hyphenate.helpdesk.easeui.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.UriUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    /**
     * 检测网络是否可用
     *
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager != null ? mConnectivityManager.getActiveNetworkInfo() : null;
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        Log.d("msg", "thum image path:" + path);
        return path;
    }
    public static String getMessageDigest(Message message, Context context) {
        String digest;
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct() == Message.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//	              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.from());
                    return digest;
                } else {
//	              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                EMTextMessageBody txtBody = (EMTextMessageBody) message.body();
                switch (MessageHelper.getMessageExtType(message)) {
                    case RobotMenuMsg:
                        digest = getString(context, R.string.robot_menu);
                        break;
                    case BigExpressionMsg:
                        if (!TextUtils.isEmpty(txtBody.getMessage())) {
                            digest = txtBody.getMessage();
                        } else {
                            digest = getString(context, R.string.dynamic_expression);
                        }
                        break;
                    default:
                        digest = txtBody.getMessage();
                        break;
                }
                break;
            case FILE: //普通文件消息
                digest = getString(context, R.string.file);
                break;
            default:
                Log.e(TAG, "error, unknow type");
                return "";
        }

        return digest;
    }


    public static boolean isSingleActivity(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager != null ? activityManager.getRunningTasks(1) : null;
        return tasks.get(0).numRunning == 1;
    }

    /**
     * 判断是否是视频文件
     * @param context
     * @param filename
     * @return
     */
    public static boolean isVideoFile(Context context, String filename) {
        return checkSuffix(filename, context.getResources().getStringArray(R.array.ease_video_file_suffix));
    }

    /**
     * open file
     *
     * @param f
     * @param context
     */
    public static void openFile(File f, Activity context) {
        /* get uri */
        Uri uri = getUriForFile(context, f);
        //为了解决本地视频文件打不开的问题
        if(isVideoFile(context, f.getName())) {
            uri = Uri.parse(f.getAbsolutePath());
        }
        openFile(uri, context);
    }

    public static void openFile(Uri uri, Activity context) {
        String mimeType = UriUtils.getMimeType(context, uri);
        if(TextUtils.isEmpty(mimeType) || TextUtils.equals(mimeType, "application/octet-stream")) {
            mimeType = getMimeType(context, UriUtils.getFileNameByUri(context, uri));
        }
        EMLog.d(TAG, "mimeType = "+mimeType);
        openFile(uri, mimeType, context);
    }

    public static void openFile(Uri uri, String type, Activity context) {
        if(openApk(context, uri)) {
            return;
        }
        EMLog.e(TAG, "openFile uri = "+uri + " type = "+type);
        String filename = UriUtils.getFileNameByUri(context, uri);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        setIntentByType(context, filename, intent);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        /* set intent's file and MimeType */
        intent.setDataAndType(uri, type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e(TAG, e.getMessage());
            Toast.makeText(context, "Can't find proper app to open this file", Toast.LENGTH_LONG).show();
        }
    }

    public static Uri getUriForFile(Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static void openFileEx(File file, String fileType, Context context) {
        try {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider",file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, fileType);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void setIntentByType(Context context, String filename, Intent intent) {
        Resources rs = context.getResources();
        if(checkSuffix(filename, rs.getStringArray(R.array.ease_audio_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_video_file_suffix))) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
        }else if(checkSuffix(filename, rs.getStringArray(R.array.ease_image_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else if(checkSuffix(filename, rs.getStringArray(R.array.ease_excel_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_word_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_pdf_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    public static boolean openApk(Context context, Uri uri) {
        String filename = UriUtils.getFileNameByUri(context, uri);
        String filePath = UriUtils.getFilePath(context, uri);
        if(filename.endsWith(".apk")) {
            if(TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
                Toast.makeText(context, "Can't find proper app to open this file", Toast.LENGTH_LONG).show();
                return true;
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", new File(filePath));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, getMimeType(context, filename));
                context.startActivity(intent);
            }else {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(Uri.fromFile(new File(filePath)), getMimeType(context, filename));
                context.startActivity(installIntent);
            }
            return true;
        }
        return false;
    }

    /**
     * 检查后缀
     * @param filename
     * @param fileSuffix
     * @return
     */
    private static boolean checkSuffix(String filename, String[] fileSuffix) {
        if(TextUtils.isEmpty(filename) || fileSuffix == null || fileSuffix.length <= 0) {
            return false;
        }
        int length = fileSuffix.length;
        for(int i = 0; i < length; i++) {
            String suffix = fileSuffix[i];
            if(filename.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static String getMimeType(Context context, String filename) {
        String mimeType = null;
        Resources resources = context.getResources();
        //先设置常用的后缀

        if(checkSuffix(filename, resources.getStringArray(R.array.ease_image_file_suffix))) {
            mimeType = "image/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_video_file_suffix))) {
            mimeType = "video/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_audio_file_suffix))) {
            mimeType = "audio/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_file_file_suffix))) {
            mimeType = "text/plain";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_word_file_suffix))) {
            mimeType = "application/msword";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_excel_file_suffix))) {
            mimeType = "application/vnd.ms-excel";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_pdf_file_suffix))) {
            mimeType = "application/pdf";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_apk_file_suffix))) {
            mimeType = "application/vnd.android.package-archive";
        }else {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    public static String getMap(String key) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("rar", "application/x-rar-compressed");
        map.put("jpg", "image/jpeg");
        map.put("zip", "application/zip");
        map.put("pdf", "application/pdf");
        map.put("doc", "application/msword");
        map.put("docx", "application/msword");
        map.put("wps", "application/msword");
        map.put("xls", "application/vnd.ms-excel");
        map.put("et", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.ms-excel");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("html", "text/html");
        map.put("htm", "text/html");
        map.put("txt", "text/html");
        map.put("mp3", "audio/mpeg");
        map.put("mp4", "video/mp4");
        map.put("3gp", "video/3gpp");
        map.put("wav", "audio/x-wav");
        map.put("avi", "video/x-msvideo");
        map.put("flv", "flv-application/octet-stream");
        map.put("", "*/*");

        return map.get(key.toLowerCase());
    }
}