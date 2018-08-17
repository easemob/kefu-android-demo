package com.hyphenate.helpdesk;


/* TODO
 * 1.orderinfo message in conversation is using the fake image
 * 2. handle welcome message
 * 3. handle co-existence of IM and HelpDesk
 * 4. Handle notification and connectionlistener
 * 5. push support
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.util.Log;

import java.util.Iterator;
import java.util.List;

public final class ChatClient {
    public final static String TAG = "ChatClient";
    private static ChatClient instance = null;
    private EMClient _client = null;
    private Chat chat = null;
    private ConnectionListener listener = null;
    private String userName = null;
    private boolean isInitialized = false;
    private String currentVersion = "1.0.0";
    private String tenantId = null;

    private ChatClient() {

    }

    public static ChatClient getInstance() {
        if (instance == null) {
            synchronized (ChatClient.class) {
                if (instance == null) {
                    instance = new ChatClient();
                }
            }
        }
        return instance;
    }


    public synchronized boolean init(final Context context,final Options options) {
        if (isInitialized) {
            android.util.Log.e(TAG, "sdk already initialized");
            return false;
        }
        if (context == null) {
            android.util.Log.e(TAG, "init fail, context is null");
            return false;
        }
        if (options == null) {
            android.util.Log.e(TAG, "init fail, options is null");
            return false;
        }
        if (TextUtils.isEmpty(options.appkey)) {
            android.util.Log.e(TAG, "init fail, appkey is null");
            return false;
        }
        String processAppName = getAppName(context.getApplicationContext());

        android.util.Log.d(TAG, "process app name : " + processAppName);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            android.util.Log.e(TAG, "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }
        _client = EMClient.getInstance();
        final EMOptions emoptions = new EMOptions();
        emoptions.setRequireAck(true);
        emoptions.setRequireDeliveryAck(false);
        emoptions.setAppKey(options.appkey);
        if (!TextUtils.isEmpty(options.mipushAppid) && !TextUtils.isEmpty(options.mipushAppKey)){
            emoptions.setMipushConfig(options.mipushAppid, options.mipushAppKey);
            Log.d(TAG, "MIPUSH appid:" + options.mipushAppid + ",appkey:" + options.mipushAppKey);
        }
        tenantId = options.tenantId;
        long currentTime = System.currentTimeMillis();
        _client.init(context, emoptions);
        Log.e(TAG, "im init time(ms):" + (System.currentTimeMillis() - currentTime));
        chat = new Chat(_client.chatManager());
        _client.addConnectionListener(new EMConnectionListener() {

            @Override
            public void onConnected() {
                if (listener != null)
                    listener.onConnected();
            }

            @Override
            public void onDisconnected(int errorCode) {
                int code = Error.GENERAL_ERROR;
                switch (errorCode) {
                    case EMError.USER_REMOVED:
                        code = Error.USER_REMOVED;
                        break;
                    case EMError.USER_ALREADY_LOGIN:
                        code = Error.USER_ALREADY_LOGIN;
                        break;
                    case EMError.USER_LOGIN_ANOTHER_DEVICE:
                        code = Error.USER_LOGIN_ANOTHER_DEVICE;
                        break;
                    case EMError.USER_AUTHENTICATION_FAILED:
                        code = Error.USER_AUTHENTICATION_FAILED;
                        break;
                    case EMError.USER_NOT_FOUND:
                        code = Error.USER_NOT_FOUND;
                        break;
                }
                if (listener != null)
                    listener.onDisconnected(code);
            }

        });

//        UIProvider.getInstance().init(context);
//
//        chat.addMessageListener(new Chat.MessageListener() {
//            @Override
//            public void onMessage(final List<Message> msgs) {
//                if (defaultNotification) {
//                    UIProvider.getInstance().getNotifier().onNewMesg(msgs);
//                }
//            }
//
//            @Override
//            public void onCmdMessage(List<Message> msgs) {
//
//            }
//
//            @Override
//            public void onMessageSent() {
//
//            }
//
//            @Override
//            public void onMessageStatusUpdate() {
//
//            }
//        });

        isInitialized = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     *
     * @param appContext
     * @return
     */
    private String getAppName(Context appContext) {
        int pid = android.os.Process.myPid();
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    /**
     * visitor login
     *
     * @param userName
     * @param password
     * @param callback
     */
    public void login(final String userName, String password,
                      final Callback callback) {
        _client.login(userName, password, new EMCallBack() {

            @Override
            public void onSuccess() {
                getChat().loadConversations();
                ChatClient.this.userName = userName;
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int code, String error) {
                if (callback != null) {
                    callback.onError(code, error);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });
    }

    /**
     * 退出方法
     *
     * 退出后将无法接收客服发来消息
     *
     * @param callback
     */
    public void logout(final Callback callback) {
        _client.logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                ChatClient.this.userName = null;
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int code, String error) {
                if (callback != null)
                    callback.onError(code, error);

            }

            @Override
            public void onProgress(int progress, String status) {

            }

        });

    }

    /**
     * 检测是否账号已经登录
     *
     * 建议在每次登录前都加上,意思是登录过不用再次请求登录.
     *
     * @return 已经登录过 返回true, 未登录 返回false
     */
    public boolean isLoggedin() {
        if (_client != null) {
            return _client.isLoggedInBefore();
        }
        return false;
    }

    /**
     * 检测服务器和当前用户是否连通
     *
     * 区别于:方法 isLoggedIn,因为此方法可能已经登录过,只是当前没网,SDK会自动登录不需要再次调用login方法.
     * @see #isLoggedin()
     *
     * @return 服务器和当前账号正常连接返回 true, 当前连接已断开返回false
     */
    public boolean isConnected() {
        if (_client != null) {
            return _client.isConnected();
        }
        return false;
    }

    /**
     * 为iOS的apns显示昵称而不是ID
     * 同步方法,需要在线程中调用
     *
     * @param userNick
     */
    public boolean updateNickToServer(String userNick) {
        return EMClient.getInstance().updateCurrentUserNick(userNick);
    }


    public void createAccount(final String userName, final String password,
                              final Callback callback) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    _client.createAccount(userName, password);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (HyphenateException ex) {
                    int errorCode = ex.getErrorCode();
                    if (callback != null) {
                        if (errorCode == EMError.USER_ALREADY_EXIST) {
                            callback.onError(Error.USER_ALREADY_EXIST,
                                    "user already exist");
                        } else if (errorCode == EMError.NETWORK_ERROR) {
                            callback.onError(Error.NETWORK_ERROR,
                                    "network is not available");
                        } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                            callback.onError(Error.USER_AUTHENTICATION_FAILED,
                                    "register fail without permission");
                        } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                            callback.onError(Error.USER_ILLEGAL_ARGUMENT,
                                    "illegal user name");
                        } else {
                            callback.onError(Error.GENERAL_ERROR,
                                    "general error");
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public Chat getChat() {
        return chat;
    }

    public String getCurrentUserName() {
        return userName;
    }

    public String getSDKVersion() {
        return currentVersion;
    }

    public void setDebugMode(boolean paramBoolean) {
        EMClient.getInstance().setDebugMode(paramBoolean);
    }

    public void addConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public void removeConnectionListener() {
        this.listener = null;
    }


    public interface ConnectionListener {
        public void onConnected();

        public void onDisconnected(int errorcode);
    }

    public static class Options {
        private String appkey = "";
        private String tenantId = "";
        private String mipushAppid = "";
        private String mipushAppKey = "";


        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public void setMIPushConfig(String mipushAppid, String mipushAppKey){
            this.mipushAppid = mipushAppid;
            this.mipushAppKey = mipushAppKey;
        }

    }


}
