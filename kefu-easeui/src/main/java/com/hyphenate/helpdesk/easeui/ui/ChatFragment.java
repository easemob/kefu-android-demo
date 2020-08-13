package com.hyphenate.helpdesk.easeui.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.KefuConversationManager;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.runtimepermission.PermissionsManager;
import com.hyphenate.helpdesk.easeui.runtimepermission.PermissionsResultAction;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.Config;
import com.hyphenate.helpdesk.easeui.widget.AlertDialog;
import com.hyphenate.helpdesk.easeui.widget.AlertDialog.AlertDialogUser;
import com.hyphenate.helpdesk.easeui.widget.EaseChatInputMenu;
import com.hyphenate.helpdesk.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.helpdesk.easeui.widget.ExtendMenu.EaseChatExtendMenuItemClickListener;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.easeui.widget.MessageList.MessageListItemClickListener;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.emojicon.Emojicon;
import com.hyphenate.helpdesk.manager.EmojiconManager;
import com.hyphenate.helpdesk.model.AgentIdentityInfo;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.UriUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

/**
 * 可以直接new出来使用的聊天对话页面fragment，
 * 使用时需调用setArguments方法传入IM服务号
 * app也可继承此fragment续写
 * 参数传入示例可查看demo里的ChatActivity
 */
public class ChatFragment extends BaseFragment implements ChatManager.MessageListener, EmojiconManager.EmojiconManagerDelegate {

    protected static final String TAG = ChatFragment.class.getSimpleName();
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    protected static final int REQUEST_CODE_CAMERA = 1;
    protected static final int REQUEST_CODE_LOCAL = 2;
    private static final int REQUEST_CODE_SELECT_VIDEO = 3;

    public static final int REQUEST_CODE_EVAL = 5;
    public static final int REQUEST_CODE_SELECT_FILE = 6;
    /**
     * 传入fragment的参数
     */
    protected Bundle fragmentArgs;
    protected String toChatUsername;
    protected boolean showUserNick;
    protected MessageList messageList;
    protected EaseChatInputMenu inputMenu;

    protected Conversation conversation;
    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;
    protected String cameraFilePath = null;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected Message contextMenuMessage;

    protected static final int ITEM_TAKE_PICTURE = 1;
    protected static final int ITEM_PICTURE = 2;
    protected static final int ITEM_VIDEO = 3;
    protected static final int ITEM_FILE = 4;

    protected int[] itemStrings = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_video, R.string.attach_file};
    protected int[] itemdrawables = {R.drawable.hd_chat_takepic_selector, R.drawable.hd_chat_image_selector, R.drawable.hd_chat_video_selector, R.drawable.hd_chat_file_selector};

    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_VIDEO, ITEM_FILE};
    protected int[] itemResIds = {R.id.chat_menu_take_pic, R.id.chat_menu_pic, R.id.chat_menu_video, R.id.chat_menu_file};
    private boolean isMessageListInited;
    protected MyMenuItemClickListener extendMenuItemClickListener;
    private VisitorInfo visitorInfo;
    private AgentIdentityInfo agentIdentityInfo;
    private QueueIdentityInfo queueIdentityInfo;
    private String titleName;
    protected TextView tvTipWaitCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportedHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportedHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
        ChatClient.getInstance().emojiconManager().addDelegate(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hd_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        fragmentArgs = getArguments();
        // IM服务号
        toChatUsername = fragmentArgs.getString(Config.EXTRA_SERVICE_IM_NUMBER);
        // 是否显示用户昵称
        showUserNick = fragmentArgs.getBoolean(Config.EXTRA_SHOW_NICK, false);
        //指定技能组
        queueIdentityInfo = fragmentArgs.getParcelable(Config.EXTRA_QUEUE_INFO);
        //指定客服
        agentIdentityInfo = fragmentArgs.getParcelable(Config.EXTRA_AGENT_INFO);
        visitorInfo = fragmentArgs.getParcelable(Config.EXTRA_VISITOR_INFO);

        titleName = fragmentArgs.getString(Config.EXTRA_TITLE_NAME);
        //在父类中调用了initView和setUpView两个方法
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            cameraFilePath = savedInstanceState.getString("cameraFilePath");
        }
        ChatClient.getInstance().chatManager().bindChat(toChatUsername);
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(String permission) {

            }
        });
        ChatClient.getInstance().chatManager().addAgentInputListener(agentInputListener);

        // 为测试获取账号用，无实际意义
        setUserNameView();
    }

    private void setUserNameView(){
        if (ChatClient.getInstance().isLoggedInBefore()){
            String currentUsername = ChatClient.getInstance().currentUserName();
            if (getView() != null) {
                TextView tvUname = (TextView) getView().findViewById(R.id.tv_username);
                if (tvUname != null){
                    tvUname.setText(currentUsername);
                }
            }
        }
    }

    /**
     * init view
     */
    @Override
    protected void initView() {
        // 消息列表layout
        messageList = (MessageList) getView().findViewById(R.id.message_list);
        messageList.setShowUserNick(showUserNick);
        listView = messageList.getListView();
        tvTipWaitCount = (TextView) getView().findViewById(R.id.tv_tip_waitcount);
        extendMenuItemClickListener = new MyMenuItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init();
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public void onBigExpressionClicked(Emojicon emojicon) {
	            if (!TextUtils.isEmpty(emojicon.getBigIconRemotePath())) {
                    sendCustomEmojiMessage(emojicon.getBigIconRemotePath());
                } else if (!TextUtils.isEmpty(emojicon.getIconRemotePath())) {
                    sendCustomEmojiMessage(emojicon.getIconRemotePath());
                } else if (!TextUtils.isEmpty(emojicon.getBigIconPath())) {
		            sendImageMessage(emojicon.getBigIconPath());
	            } else if (!TextUtils.isEmpty(emojicon.getIconPath())) {
                    sendImageMessage(emojicon.getIconPath());
	            }
            }

            @Override
            public void onRecorderCompleted(float seconds, String filePath) {
                // 发送语音消息
                int time = seconds > 1 ? (int) seconds : 1;
                sendVoiceMessage(filePath, time);
            }
        });
        inputMenu.setHasSendButton(true);

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ChatClient.getInstance().chatManager().addVisitorWaitListener(visitorWaitListener);
    }

    ChatManager.VisitorWaitListener visitorWaitListener = new ChatManager.VisitorWaitListener() {
	    @Override
	    public void waitCount(final int num) {
		    if (getActivity() == null){
			    return;
		    }
//            EMLog.d(TAG, "waitCount--num:" + num);
		    getActivity().runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
				    if (num > 0){
					    tvTipWaitCount.setVisibility(View.VISIBLE);
					    tvTipWaitCount.setText(getString(R.string.current_wait_count, num));
				    }else{
					    tvTipWaitCount.setVisibility(View.GONE);
				    }
			    }
		    });
	    }
    };

    ChatManager.AgentInputListener agentInputListener = new ChatManager.AgentInputListener() {
        @Override
        public void onInputState(final String input) {
            if (getActivity() == null){
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (input != null) {
                        titleBar.setTitle(input);
                    } else {
                        if (!TextUtils.isEmpty(titleName)) {
                            titleBar.setTitle(titleName);
                        } else {
                            titleBar.setTitle(toChatUsername);
                        }
                    }
                }
            });

        }
    };

    /**
     * 设置属性，监听等
     */
    @Override
    protected void setUpView() {
        if (!TextUtils.isEmpty(titleName)) {
            titleBar.setTitle(titleName);
        } else {
            titleBar.setTitle(toChatUsername);
        }

        titleBar.setRightImageResource(R.drawable.hd_mm_title_remove);

        onConversationInit();
        onMessageListInit();

        // 设置标题栏点击事件
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    getActivity().finish();
                }
            }
        });
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                emptyHistory();
            }
        });
        setRefreshLayoutListener();

        // test api
//        ChatClient.getInstance().chatManager().getTransferGuideMenu(toChatUsername, new ValueCallBack<JSONObject>() {
//            @Override
//            public void onSuccess(JSONObject value) {
//                EMLog.d(TAG, "onsuccess" + value.toString());
//                Message message = Message.createReceiveMessage(Message.Type.TXT);
//                message.setBody(new EMTextMessageBody("test guide"));
//                message.setMsgId(UUID.randomUUID().toString());
//                message.setStatus(Message.Status.SUCCESS);
//                message.setFrom(toChatUsername);
//                message.setMessageTime(System.currentTimeMillis());
//                try {
//                    message.setAttribute(Message.KEY_MSGTYPE, value.getJSONObject(Message.KEY_MSGTYPE));
//                    ChatClient.getInstance().chatManager().saveMessage(message);
//                    messageList.refreshSelectLast();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            @Override
//            public void onError(int error, String errorMsg) {
//
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatClient.getInstance().emojiconManager().removeDelegate(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatClient.getInstance().chatManager().unbindChat();
        ChatClient.getInstance().chatManager().removeAgentInputListener(agentInputListener);
        ChatClient.getInstance().chatManager().removeVisitorWaitListener(visitorWaitListener);
    }

    /**
     * 注册底部菜单扩展栏item; 覆盖此方法时如果不覆盖已有item，item的id需大于3
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], itemResIds[i], extendMenuItemClickListener);
        }
    }

    protected void onConversationInit() {
        // 获取当前conversation对象
        conversation = ChatClient.getInstance().chatManager().getConversation(toChatUsername);
        if (conversation != null) {
            // 把此会话的未读数置为0
            conversation.markAllMessagesAsRead();
            final List<Message> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).messageId();
                }
                conversation.loadMessages(msgId, pagesize - msgCount);
            }
        }

    }

    protected void onMessageListInit() {
        messageList.init(toChatUsername, chatFragmentListener != null ?
                chatFragmentListener.onSetCustomChatRowProvider() : null);
        //设置list item里的控件的点击事件
        setListItemClickListener();

        messageList.getListView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!inputMenu.isVoiceRecording()){//录音时，点击列表不做操作
                    hideKeyboard();
                    inputMenu.hideExtendMenuContainer();
                }
                return false;
            }
        });

        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentListener != null) {
                    chatFragmentListener.onAvatarClick(username);
                }
            }

            @Override
            public void onResendClick(final Message message) {
                new AlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        ChatClient.getInstance().chatManager().resendMessage(message);
                    }
                }, true).show();
            }

            @Override
            public void onBubbleLongClick(Message message) {
                contextMenuMessage = message;
                if (chatFragmentListener != null) {
                    chatFragmentListener.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(Message message) {
                if (chatFragmentListener != null) {
                    return chatFragmentListener.onMessageBubbleClick(message);
                }
                return false;
            }

            @Override
            public void onMessageItemClick(Message message, MessageList.ItemAction action) {
                contextMenuMessage = message;
                if (chatFragmentListener != null) {
                    chatFragmentListener.onMessageItemClick(message, action);
                }
            }
        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<Message> messages = null;
                            try {
                                messages = conversation.loadMessages(messageList.getItem(0).messageId(),
                                        pagesize);
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages != null && messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            ToastHelper.show(getActivity(), R.string.no_more_messages);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                sendImageMessage(cameraFilePath);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        sendFileByUri(uri);
                    }
                }
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                if (data != null) {
                    int duration = data.getIntExtra("dur", 0);
                    String videoPath = data.getStringExtra("path");
                    String uriString = data.getStringExtra("uri");
                    EMLog.d(TAG, "path = " + videoPath + " uriString = " + uriString);
                    if (!TextUtils.isEmpty(videoPath)) {
                        File file = new File(PathUtil.getInstance().getVideoPath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                            EMLog.e(TAG, e.getMessage());
                        }
                    } else {
                        Uri videoUri = UriUtils.getLocalUriFromString(uriString);
                        File file = new File(PathUtil.getInstance().getVideoPath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            MediaMetadataRetriever media = new MediaMetadataRetriever();
                            media.setDataSource(getContext(), videoUri);
                            Bitmap frameAtTime = media.getFrameAtTime();
                            frameAtTime.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoUri, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageList.refresh();
        MediaManager.resume();
        UIProvider.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        ChatClient.getInstance().chatManager().addMessageListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        ChatClient.getInstance().chatManager().removeMessageListener(this);
        // 把此activity 从foreground activity 列表里移除
        UIProvider.getInstance().popActivity(getActivity());
    }


    public void onBackPressed() {
        inputMenu.onBackPressed();
    }

    @Override
    public void onEmojiconChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastHelper.show(getActivity(), R.string.emoji_icon_update);
                if (inputMenu != null) {
                    inputMenu.onEmojiconChanged();
                }
            }
        });
    }

    /**
     * 扩展菜单栏item点击事件
     */
    class MyMenuItemClickListener implements EaseChatExtendMenuItemClickListener {

        @Override
        public void onExtendMenuItemClick(int itemId, View view) {
            if (getActivity() == null){
                return;
            }
            if (chatFragmentListener != null) {
                if (chatFragmentListener.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE: // 拍照
                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(ChatFragment.this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                        @Override
                        public void onGranted() {
                            selectPicFromCamera();
                        }

                        @Override
                        public void onDenied(String permission) {

                        }
                    });
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal(); // 图库选择图片
                    break;
                case ITEM_VIDEO:
                    PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(ChatFragment.this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                        @Override
                        public void onGranted() {
                            selectVideoFromLocal();
                        }

                        @Override
                        public void onDenied(String permission) {

                        }
                    });

                    break;
                case ITEM_FILE:
                    //一般文件
                    //demo这里是通过系统api选择文件，实际app中最好是做成qq那种选择发送文件
                    selectFileFromLocal();
                    break;
                default:
                    break;
            }
        }

    }

    private void selectVideoFromLocal() {
        Intent intent = new Intent(getActivity(), ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    /**
     * 选择文件
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { //19以后这个api不可用，demo这里简单处理成图库选择图片
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }


    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        sendImageMessage(selectedImage);
    }

    /**
     * 根据uri发送文件
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        sendFileMessage(uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        if (cameraFilePath != null){
            outState.putString("cameraFile", cameraFilePath);
        }
    }

    /**
     * 照相获取图片
     */
    protected void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastHelper.show(getActivity(), R.string.sd_card_does_not_exist);
            return;
        }
        try{
            File cameraFile = new File(PathUtil.getInstance().getImagePath(), ChatClient.getInstance().currentUserName()
                    + System.currentTimeMillis() + ".jpg");
            cameraFilePath = cameraFile.getAbsolutePath();
            if (!cameraFile.getParentFile().exists()){
                cameraFile.getParentFile().mkdirs();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            } else {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext().getApplicationContext(), getContext().getPackageName() +  ".fileProvider", cameraFile));
            }
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从图库获取图片
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * 点击清空聊天记录
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new AlertDialog(getActivity(), null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    MediaManager.release();
                    ChatClient.getInstance().chatManager().clearConversation(toChatUsername);
                    messageList.refresh();
                }
            }
        }, true).show();
    }


    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected EaseChatFragmentListener chatFragmentListener;

    public void setChatFragmentListener(EaseChatFragmentListener chatFragmentListener) {
        this.chatFragmentListener = chatFragmentListener;
    }

    public interface EaseChatFragmentListener {
        /**
         * 用户头像点击事件
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * 消息气泡框点击事件
         */
        boolean onMessageBubbleClick(Message message);

        /**
         * 消息气泡框长按事件
         */
        void onMessageBubbleLongClick(Message message);

        /**
         * 扩展输入栏item点击事件,如果要覆盖EaseChatFragment已有的点击事件，return true
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * 菜单消息被点击有具体的跳转要求时执行下面的回调
         */
        void onMessageItemClick(Message message, MessageList.ItemAction action);

        /**
         * 设置自定义chatrow提供者
         *
         * @return
         */
        CustomChatRowProvider onSetCustomChatRowProvider();
    }

    @Override
    public void onMessage(List<Message> msgs) {
        for (Message message : msgs) {
            String username = null;
            username = message.from();

            // 如果是当前会话的消息，刷新聊天页面
            if (username != null && username.equals(toChatUsername)) {
                messageList.refreshSelectLast();
                // 声音和震动提示有新消息
                UIProvider.getInstance().getNotifier().viberateAndPlayTone(message);
            } else {
                // 如果消息不是和当前聊天ID的消息
                UIProvider.getInstance().getNotifier().onNewMsg(message);
            }
        }

    }

    @Override
    public void onCmdMessage(List<Message> msgs) {

    }


    @Override
    public void onMessageStatusUpdate() {
        messageList.refreshSelectLast();
    }

    @Override
    public void onMessageSent() {
        messageList.refreshSelectLast();
    }

    // 发送消息方法
    //=============================================
    protected void sendTextMessage(String content) {
        if (content != null && content.length() > 1500){
            ToastHelper.show(getActivity(), R.string.message_content_beyond_limit);
            return;
        }
        Message message = Message.createTxtSendMessage(content, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLast();
    }

    protected void sendVoiceMessage(String filePath, int length) {
        if (TextUtils.isEmpty(filePath)){
            return;
        }
        Message message = Message.createVoiceSendMessage(filePath, length, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLast();
    }

    protected void sendImageMessage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        File imageFile = new File(imagePath);
        if (!imageFile.exists()){
            return;
        }

        Message message = Message.createImageSendMessage(imagePath, false, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendImageMessage(Uri imageUri) {
        Message message = Message.createImageSendMessage(imageUri, false, toChatUsername);
        if (message != null) {
            attachMessageAttrs(message);
            ChatClient.getInstance().chatManager().sendMessage(message);
            messageList.refreshSelectLastDelay(MessageList.defaultDelay);
        }
    }

    protected void sendCustomEmojiMessage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)){
            return;
        }

        Message message = Message.createCustomEmojiSendMessage(imagePath, toChatUsername);
        message.setMessageTime(System.currentTimeMillis());
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendFileMessage(String filePath) {
        Message message = Message.createFileSendMessage(filePath, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendFileMessage(Uri fileUri) {
        Message message = Message.createFileSendMessage(fileUri, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress, String toChatUsername){
        Message message = Message.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        Message message = Message.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    protected void sendVideoMessage(Uri videoUri, String thumbPath, int videoLength) {
        Message message = Message.createVideoSendMessage(videoUri, thumbPath, videoLength, toChatUsername);
        attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLastDelay(MessageList.defaultDelay);
    }

    public void attachMessageAttrs(Message message){
        if (visitorInfo != null){
            message.addContent(visitorInfo);
        }
        if (queueIdentityInfo != null){
            message.addContent(queueIdentityInfo);
        }
        if (agentIdentityInfo != null){
            message.addContent(agentIdentityInfo);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MediaManager.pause();
    }



}
