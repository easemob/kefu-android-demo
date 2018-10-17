package com.hyphenate.helpdesk.easeui.util;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.PathUtil;

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

    public static void openFileEx(File file, String fileType, Context context) {
        try {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".ease",file);
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