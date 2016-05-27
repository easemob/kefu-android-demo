package com.easemob.helpdeskdemo.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.helpdeskdemo.Constant;
import com.easemob.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 欢迎语(系统欢迎语 机器人欢迎语)
 *
 */
public class WelcomeMessageHandler implements Parcelable {

    private static final String TAG = WelcomeMessageHandler.class.getSimpleName();

    public static final String EXTRA_WELCOME_MESSAGE = "extra_welcome_message";
    //kefu host
    private static final String HOST = "http://kefu.easemob.com";
    // robot welcome url
    private static final String URL_ROBOT = HOST + "/v1/Tenants/%s/robots/visitor/greetings?tenantId=%s";
    // get welcome url
    private static final String URL = HOST + "/v1/tenantapi/welcome?tenantId=%s&orgName=%s&appName=%s&userName=%s&token=%s";
    // check session isexist url
    private static final String URL_SESSION = HOST + "/v1/webimplugin/visitors/%s/schedule-data?techChannelInfo=%s&tenantId=%s";

    private String tenantId;
    private String currentUser;
    private String imServiceUser;
    private ExecutorService mSingleExecutor = Executors.newSingleThreadExecutor();

    public static WelcomeMessageHandler instance;

    public static WelcomeMessageHandler getInstance(String tenantId, String imServiceUser) {
        if (instance == null) {
            instance = new WelcomeMessageHandler(tenantId, imServiceUser);
        }
        return instance;
    }


    private WelcomeMessageHandler(String tenantId, String imServiceUser) {
        this.tenantId = tenantId;
        this.currentUser = EMChatManager.getInstance().getCurrentUser();
        this.imServiceUser = imServiceUser;
    }


    protected WelcomeMessageHandler(Parcel in) {
        tenantId = in.readString();
        currentUser = in.readString();
        imServiceUser = in.readString();
    }

    public static final Creator<WelcomeMessageHandler> CREATOR = new Creator<WelcomeMessageHandler>() {
        @Override
        public WelcomeMessageHandler createFromParcel(Parcel in) {
            return new WelcomeMessageHandler(in);
        }

        @Override
        public WelcomeMessageHandler[] newArray(int size) {
            return new WelcomeMessageHandler[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tenantId);
        dest.writeString(currentUser);
        dest.writeString(imServiceUser);
    }

    /**
     * 查询会话是否建立 已经建立的(会话中)不发欢迎语,不存在会话(发欢迎语)
     *
     * @return
     */
    public boolean getSessionIsExist() {
        String channalInfo = EMChat.getInstance().getAppkey() + "#" + imServiceUser;
        try {
            channalInfo = URLEncoder.encode(channalInfo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_SESSION, currentUser, channalInfo, tenantId);
        JSONObject jsonObj = null;
        try {
            String result = HttpGet(url);
            if (!TextUtils.isEmpty(result)) {
                jsonObj = new JSONObject(result);
                return !(jsonObj.isNull("serviceSession"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//"/v1/tenantapi/welcome?tenantId=%s&orgName=%s&appName=%s&userName=%s&token=%s";
    public String getWelcomeMessage() {
        String appKey = EMChat.getInstance().getAppkey();
        if (TextUtils.isEmpty(appKey)){
            return null;
        }
        try {
            String orgName = appKey.split("#")[0];
            String appName = appKey.split("#")[1];
            String currentUser = EMChatManager.getInstance().getCurrentUser();
            String token = EMChatManager.getInstance().getAccessToken();

            String remoteUrl = String.format(URL, tenantId, orgName, appName, currentUser, token);
            return HttpGet(remoteUrl);
        } catch (Exception e) {
            EMLog.e(TAG, "welcome-message:" + e.getMessage());
        }
        return null;
    }

    public JSONObject getRobotWelcomeMessage() {
        JSONObject jsonObj = null;
        String url = String.format(URL_ROBOT, tenantId, tenantId);
        try {
            String result = HttpGet(url);
            if (!TextUtils.isEmpty(result)) {
                jsonObj = new JSONObject(result);
                String menu = jsonObj.getString("greetingText").replace("&quot;", "\"");
                jsonObj = jsonObj.put("greetingText", menu);
            }
        } catch (Exception e) {
            EMLog.e(TAG, "robot-welcome-message:" + e.getMessage());
        }
        return jsonObj;
    }

    public String HttpGet(String remoteUrl) throws IOException {
        URL url = new URL(remoteUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.connect();
        int resCode = conn.getResponseCode();
        if (resCode == 200) {
            return new String(readInputStream(conn.getInputStream()));
        } else if (resCode == 401) {
            EMLog.e(TAG, "resCode is 401, UNAUTHORIZED");
            return null;
        } else {
            EMLog.e(TAG, "resCode is " + resCode);
            return null;
        }

    }


    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }


    /**
     *  添加系统欢迎语
     * @param welcomeMsg
     */
    public void importWelcomeMessage(String welcomeMsg) {
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.addBody(new TextMessageBody(welcomeMsg));
        message.setFrom(imServiceUser);
        message.setTo(currentUser);
        message.setMsgId(UUID.randomUUID().toString());
        message.setAttribute(WelcomeMessageHandler.EXTRA_WELCOME_MESSAGE, true);
        EMChatManager.getInstance().importMessage(message, true);
    }


    /**
     * 添加机器人欢迎语
     * @param jsonRobotResult
     */
    public void importRobotWelcomeMessage(JSONObject jsonRobotResult) {
        try {
            int textType = jsonRobotResult.getInt("greetingTextType");
            String greetingText = jsonRobotResult.getString("greetingText");
            if (textType == 0) {
                importWelcomeMessage(greetingText);
            } else if (textType == 1) {
                JSONObject jsonMenu = new JSONObject(greetingText);
                JSONObject jsonExt = jsonMenu.getJSONObject("ext");
                if (jsonExt.has("msgtype")) {
                    JSONObject jsonMsgType = jsonExt.getJSONObject("msgtype");
                    EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    message.addBody(new TextMessageBody("menu-message"));
                    message.setFrom(imServiceUser);
                    message.setTo(currentUser);
                    message.setMsgId(UUID.randomUUID().toString());
                    message.setAttribute(WelcomeMessageHandler.EXTRA_WELCOME_MESSAGE, true);
                    message.setAttribute(Constant.MESSAGE_ATTR_MSGTYPE, jsonMsgType);
                    EMChatManager.getInstance().importMessage(message, true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void execute(Runnable runnable){
        mSingleExecutor.execute(runnable);
    }


}
