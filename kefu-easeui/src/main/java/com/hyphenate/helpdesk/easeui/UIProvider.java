package com.hyphenate.helpdesk.easeui;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.emojicon.Emojicon;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UIProvider {
    private static final String TAG = UIProvider.class.getSimpleName();

    /**
     * the global EaseUI instance
     */
    private static UIProvider instance = null;

    /**
     * 用户属性提供者
     */
    private UserProfileProvider userProvider;

    private SettingsProvider settingsProvider;

    /**
     * application context
     */
    private Context appContext = null;

    /**
     * the notifier
     */
    private Notifier notifier = null;

    private boolean showProgress = false;

    /**
     * 用来记录注册了eventlistener的foreground Activity
     */
    private List<Activity> activityList = Collections.synchronizedList(new ArrayList<Activity>());

    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity);
        }
    }

    public void popActivity(Activity activity){
        activityList.remove(activity);
    }


    private UIProvider(){}

    /**
     * 获取EaseUI单实例对象
     * @return
     */
    public synchronized static UIProvider getInstance(){
        if(instance == null){
            instance = new UIProvider();
        }
        return instance;
    }

    public void setShowProgress(boolean isShowProgress){
        this.showProgress = isShowProgress;
    }

    public boolean isShowProgress(){
        return showProgress;
    }


    /**
     * @param context
     * @return
     */
    public synchronized void init(final Context context){
        appContext = context;
        initNotifier();

        if(settingsProvider == null){
            settingsProvider = new DefaultSettingsProvider();
        }

        ChatClient.getInstance().getChat().addMessageListener(new ChatManager.MessageListener() {
            @Override
            public void onMessage(List<Message> msgs) {
                if (!EasyUtils.isAppRunningForeground(context)){
                    UIProvider.getInstance().getNotifier().onNewMesg(msgs);
                }
            }

            @Override
            public void onCmdMessage(List<Message> msgs) {

            }

            @Override
            public void onMessageStatusUpdate() {

            }

            @Override
            public void onMessageSent() {

            }
        });
    }

    void initNotifier(){
        notifier = createNotifier();
        notifier.init(appContext);
    }

    protected Notifier createNotifier(){
        return new Notifier();
    }

    public Notifier getNotifier(){
        return notifier;
    }

    public boolean hasForegroundActivies(){
        return activityList.size() != 0;
    }

    /**
     * 设置用户属性提供者
     * @param userProvider
     */
    public void setUserProfileProvider(UserProfileProvider userProvider){
        this.userProvider = userProvider;
    }

    /**
     * 获取用户属性提供者
     * @return
     */
    public UserProfileProvider getUserProfileProvider(){
        return userProvider;
    }

    public void setSettingsProvider(SettingsProvider settingsProvider){
        this.settingsProvider = settingsProvider;
    }

    public SettingsProvider getSettingsProvider(){
        return settingsProvider;
    }


    public interface UserProfileProvider {
        void setNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView);
    }

    /**
     * 表情信息提供者
     *
     */
    public interface EmojiconInfoProvider {
        /**
         * 根据唯一识别号返回此表情内容
         * @param emojiconIdentityCode
         * @return
         */
        Emojicon getEmojiconInfo(String emojiconIdentityCode);

        /**
         * 获取文字表情的映射Map,map的key为表情的emoji文本内容，value为对应的图片资源id或者本地路径(不能为网络地址)
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }

    private EmojiconInfoProvider emojiconInfoProvider;

    /**
     * 获取表情提供者
     * @return
     */
    public EmojiconInfoProvider getEmojiconInfoProvider(){
        return emojiconInfoProvider;
    }

    /**
     * 设置表情信息提供者
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EmojiconInfoProvider emojiconInfoProvider){
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    /**
     * 新消息提示设置的提供者
     *
     */
    public interface SettingsProvider {
        boolean isMsgNotifyAllowed(Message message);
        boolean isMsgSoundAllowed(Message message);
        boolean isMsgVibrateAllowed(Message message);
        boolean isSpeakerOpened();
    }

    /**
     * default settings provider
     *
     */
    protected class DefaultSettingsProvider implements SettingsProvider{

        @Override
        public boolean isMsgNotifyAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isMsgSoundAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isMsgVibrateAllowed(Message message) {
            return true;
        }

        @Override
        public boolean isSpeakerOpened() {
            return true;
        }


    }


}