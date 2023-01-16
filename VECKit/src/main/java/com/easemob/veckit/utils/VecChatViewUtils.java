package com.easemob.veckit.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.easemob.veckit.BlankActivity;
import com.easemob.veckit.R;
import com.easemob.veckit.ui.BottomContainer;
import com.easemob.veckit.ui.BottomContainerView;
import com.easemob.veckit.ui.IconTextView;
import com.easemob.veckit.ui.widget.EaseChatInputMenu;
import com.easemob.veckit.ui.widget.MessageList;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.emojicon.Emojicon;

import java.util.ArrayList;
import java.util.List;


public class VecChatViewUtils implements View.OnLayoutChangeListener, View.OnClickListener, ChatManager.MessageListener {
    private Context mContext;
    private View mShowView;
    private int mHeight;
    private boolean mIsOpenKeyBoard;
    private View mLltVecChatView;
    private Rect mRect;
    private View mVecChatTv;
    private InputMethodManager mImm;
    private EditText mVecChatEdt;
    private View mOneItemFlt;
    private View mTwoItemFlt;
    private BottomContainerView.OnViewPressStateListener mOnViewPressStateListener;
    private BottomContainer mBottomContainer;

    private boolean mIsNewStyle = false;
    private IconTextView mOneIcon;
    private IconTextView mTwoIcon;
    private View mChatView;
    private MessageList mMessageList;
    private TextView mToastTv;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    protected Conversation conversation;
    protected boolean mIsLoading;
    protected boolean mHaveMoreData = true;
    protected int mPageSize = 20;

    public boolean isNewStyle() {
        return mIsNewStyle;
    }

    public VecChatViewUtils(Context context, IVecChatViewCallback chatViewCallback){
        mContext = context.getApplicationContext();
        this.mIVecChatViewCallback = chatViewCallback;
    }

    public void initView(View showView, int height, int chatViewHeight) {
        mShowView = showView;
        mHeight = height;
        mImm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        // EaseChatPrimaryMenu
        mChatView = mShowView.findViewById(R.id.chatView);
        mLltVecChatView = mShowView.findViewById(R.id.lltVecChatView);
        mToastTv = mLltVecChatView.findViewById(R.id.toastTv);
        clip(mToastTv);
        mMessageList = mShowView.findViewById(R.id.message_list);
        mListView = mMessageList.getListView();
        mSwipeRefreshLayout = mMessageList.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        mMessageList.init(AgoraMessage.newAgoraMessage().getCurrentChatUsername(), null);
        setListItemClickListener();

        ViewGroup.LayoutParams chatViewParams = mChatView.getLayoutParams();
        if (chatViewParams != null){
            chatViewParams.height = chatViewHeight;
            mChatView.setLayoutParams(chatViewParams);
        }

        ViewGroup.LayoutParams layoutParams = mLltVecChatView.getLayoutParams();
        if (layoutParams != null){
            layoutParams.height = chatViewHeight;
            mLltVecChatView.setLayoutParams(layoutParams);
        }

        mVecChatTv = mShowView.findViewById(R.id.vecChatTvClose);
        mVecChatEdt = mShowView.findViewById(R.id.vecChatEdt);
        mVecChatTv.setOnClickListener(this);
        mRect = new Rect();
        mShowView.addOnLayoutChangeListener(this);

        initKeyboard(mShowView);
        ChatClient.getInstance().chatManager().addMessageListener(this);
    }

    private EaseChatInputMenu mInputMenu;
    private void initKeyboard(View showView) {
        mInputMenu = (EaseChatInputMenu) showView.findViewById(R.id.input_menu);

        mInputMenu.init();
        mInputMenu.setHasSendButton(true);
        mInputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                // 发送文本消息
                sendTextMessage(content);
            }

            @Override
            public void onBigExpressionClicked(Emojicon emojicon) {
                /*if (!TextUtils.isEmpty(emojicon.getBigIconRemotePath())) {
                    sendCustomEmojiMessage(emojicon.getBigIconRemotePath());
                } else if (!TextUtils.isEmpty(emojicon.getIconRemotePath())) {
                    sendCustomEmojiMessage(emojicon.getIconRemotePath());
                } else if (!TextUtils.isEmpty(emojicon.getBigIconPath())) {
                    sendImageMessage(emojicon.getBigIconPath());
                } else if (!TextUtils.isEmpty(emojicon.getIconPath())) {
                    sendImageMessage(emojicon.getIconPath());
                }*/
            }

            @Override
            public void onRecorderCompleted(float seconds, String filePath) {
                // 发送语音消息
                int time = seconds > 1 ? (int) seconds : 1;
                //sendVoiceMessage(filePath, time);
            }

            @Override
            public void showGallery() {
                /*Intent intent = new Intent(mContext, BlankActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);*/
                startBlankActivity(BlankActivity.IMAGE_REQUEST, null, null, null);
            }
        });
    }

    protected void onConversationInit() {
        // 获取当前conversation对象
        conversation = ChatClient.getInstance().chatManager().getConversation(AgoraMessage.newAgoraMessage().getCurrentChatUsername());
        if (conversation != null) {
            // 把此会话的未读数置为0
            conversation.markAllMessagesAsRead();
            final List<Message> msgs = conversation.getAllMessages();
            int msgCount = msgs != null ? msgs.size() : 0;
            if (msgCount < conversation.getAllMsgCount() && msgCount < mPageSize) {
                String msgId = null;
                if (msgs != null && msgs.size() > 0) {
                    msgId = msgs.get(0).messageId();
                }
                conversation.loadMessages(msgId, mPageSize - msgCount);
            }
        }

    }

    protected void setRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mIsFinishing) {
                            return;
                        }
                        if (mListView.getFirstVisiblePosition() == 0 && !mIsLoading && mHaveMoreData) {
                            List<Message> messages = null;
                            try {
                                messages = conversation.loadMessages(mMessageList.getItem(0).messageId(),
                                        mPageSize);
                            } catch (Exception e1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages != null && messages.size() > 0) {
                                mMessageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != mPageSize) {
                                    mHaveMoreData = false;
                                }
                            } else {
                                mHaveMoreData = false;
                            }

                            mIsLoading = false;

                        } else {
                            showToast(Utils.getString(mContext, R.string.no_more_messages));
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (mShowView  == null || mLltVecChatView == null){
            return;
        }
        mShowView.getWindowVisibleDisplayFrame(mRect);

        int height = Math.abs(mHeight - (mRect.bottom - mRect.top));
        if (height > mHeight / 4){
            mIsOpenKeyBoard = true;
            mLltVecChatView.setTranslationY(-height);
        }else {
            mIsOpenKeyBoard = false;
            mLltVecChatView.setTranslationY(0);
        }
    }

    public void closeChatView(){
        mIsShowChatView = false;
        showAndHidden(mChatView, false);
        showAndHidden(mLltVecChatView, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.vecChatTvClose){
            // 关闭
            if (mIsOpenKeyBoard){
                closeKeyBoard();
            }
            closeChatView();
        }else if (id == R.id.oneItemFlt || id == R.id.twoItemFlt){
            if (mIconDatas != null){
                Integer index = (Integer) v.getTag();
                BottomContainerView.ViewIconData data = mIconDatas.get(index);
                if (mOnViewPressStateListener == null && mBottomContainer != null){
                    mOnViewPressStateListener = mBottomContainer.getOnViewPressStateListener();
                }
                if (mOnViewPressStateListener != null){
                    mOnViewPressStateListener.onPressStateChange(index, data.isClickState(), data.isCustomState());
                }
            }
        }
    }

    private void showKeyBoard(){
        if (mImm != null && mVecChatEdt != null){
            mImm.showSoftInput(mVecChatEdt,0);
        }
    }

    private void closeKeyBoard(){
        if (mImm != null && mVecChatEdt != null){
            mImm.hideSoftInputFromWindow(mVecChatEdt.getWindowToken(), 0);
        }
    }

    private void showAndHidden(View view, boolean isShow) {
        if (view == null){
            return;
        }

        if (isShow && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            return;
        }

        if (!isShow && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public List<BottomContainerView.ViewIconData> getIcons(){
        return mIconDatas;
    }

    private List<BottomContainerView.ViewIconData> mIconDatas = new ArrayList<>();
    public void initIcon(BottomContainer bottomContainer){
        if (mIconDatas != null){
            for (BottomContainerView.ViewIconData iconData : mIconDatas){
                iconData.onDestroy();
            }
            mIconDatas.clear();
        }
        mBottomContainer = bottomContainer;

        // 麦克风
        BottomContainerView.ViewIconData voiceData = new BottomContainerView.ViewIconData(
                "\ue6ef", "#3B84F7",
                "\ue6a7", "#ff4400",
                BottomContainerView.ViewIconData.TYPE_ITEM_VOICE);
        voiceData.setState(true);
        mIconDatas.add(voiceData);

        // 相机
        BottomContainerView.ViewIconData cameData = new BottomContainerView.ViewIconData(
                true,
                "\ue76c", "#3B84F7",
                "\ue640", "#ff4400",
                true,
                BottomContainerView.ViewIconData.TYPE_ITEM_CAME);
        cameData.setState(true);
        mIconDatas.add(cameData);

        // 电话
        mIconDatas.add(new BottomContainerView.ViewIconData("\ue605", "#ff4400",
                "\ue605", "#3B84F7", 56, true,
                BottomContainerView.ViewIconData.TYPE_ITEM_PHONE));

        // 消息
        BottomContainerView.ViewIconData four = new BottomContainerView.ViewIconData(true,
                "\ue719", "#3B84F7",
                "\ue719", "#ff4400",
                true, BottomContainerView.ViewIconData.TYPE_ITEM_MESSAGE);
        four.setTextSize(26);
        four.setHasNum(true);
        mIconDatas.add(four);

        // 更多
        BottomContainerView.ViewIconData five = new BottomContainerView.ViewIconData(
                "\ue64c", "#3B84F7",
                "\ue64c", "#ff4400",
                true, BottomContainerView.ViewIconData.TYPE_ITEM_MORE);
        five.setTextSize(26);
        mIconDatas.add(five);

        // 其它灰度功能
        addIcon();
        bottomContainer.addIcons(mIconDatas);
    }

    public int isHavMoreView(){
        if (mIconDatas != null){
            return mIconDatas.size() - 5;
        }

        return 0;
    }

    public BottomContainerView.ViewIconData getViewIconData(int index){
        if (mIconDatas != null && index < mIconDatas.size()){
            return mIconDatas.get(index);
        }
        return null;
    }

    public BottomContainerView.ViewIconData getMoreViewIconData(int index){
        index = index + 5;
        if (mIconDatas != null && index < mIconDatas.size()){
            return mIconDatas.get(index);
        }
        return null;
    }

    // 添加灰度
    private void addIcon(){
        //List<FunctionIconItem> iconItems = FlatFunctionUtils.get().getIconItems();

        List<FunctionIconItem> iconItems = new ArrayList<>();
        FunctionIconItem iconItem1 = new FunctionIconItem("shareDesktop");
        iconItem1.setStatus("enable");
        FunctionIconItem iconItem2 = new FunctionIconItem("whiteBoard");
        iconItem2.setStatus("enable");
        iconItems.add(iconItem1);
        iconItems.add(iconItem2);

        FunctionIconItem shareDesktop = getFunctionIconItem(iconItems, "shareDesktop");
        if (shareDesktop != null && shareDesktop.isEnable()) {
            // 分享
            mIconDatas.add(new BottomContainerView.ViewIconData(true, "\ue6ff", "#3B84F7", "\ue6ff", "#ff4400", false, BottomContainerView.ViewIconData.TYPE_ITEM_SHARE));
        }

        FunctionIconItem whiteBoard = getFunctionIconItem(iconItems, "whiteBoard");
        if (whiteBoard != null && whiteBoard.isEnable()) {
            // 白板
            mIconDatas.add(new BottomContainerView.ViewIconData("\ue6a5", "#3B84F7", "\ue6a5", "#ff4400", true, BottomContainerView.ViewIconData.TYPE_ITEM_FLAT));
        }
    }

    private FunctionIconItem getFunctionIconItem(List<FunctionIconItem> functionIconItems, String name) {
        for (FunctionIconItem iconItem : functionIconItems) {
            if (name.equalsIgnoreCase(iconItem.getGrayName())) {
                return iconItem;
            }
        }

        return null;
    }

    private Handler mHandler = new Handler();
    private void showToast(String msg){
        if (mHandler != null){
            mToastTv.setText(msg);
            showAndHidden(mToastTv, true);
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAndHidden(mToastTv, false);
                }
            }, 3000);
        }
    }



    private boolean mIsFinishing;
    public void clear(){
        mIsFinishing = true;
        mRect = null;
        mHeight = 0;
        ChatClient.getInstance().chatManager().removeMessageListener(this);
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mOnViewPressStateListener = null;
        mIVecChatViewCallback = null;
        if (mOneItemFlt != null){
            mOneItemFlt.setOnClickListener(null);
            mOneItemFlt = null;
        }

        if (mTwoItemFlt != null){
            mTwoItemFlt.setOnClickListener(null);
            mTwoItemFlt = null;
        }

        if (mVecChatTv != null){
            mVecChatTv.setOnClickListener(null);
            mVecChatTv = null;
        }


        if (mIconDatas != null){
            for (BottomContainerView.ViewIconData data : mIconDatas){
                data.onDestroy();
            }
            mIconDatas.clear();
            mIconDatas = null;
        }

        if (mVecChatEdt != null){
            mVecChatEdt = null;
        }

        if (mShowView != null){
            mShowView.removeOnLayoutChangeListener(this);
        }

        if (mBottomContainer != null){
            mBottomContainer = null;
        }

        mOneIcon = null;
        mTwoIcon = null;
        mChatView = null;
        mLltVecChatView = null;
        mContext = null;
        mShowView = null;
    }

    public String getContent(BottomContainerView.ViewIconData moreViewIconData) {
        if (BottomContainerView.ViewIconData.TYPE_ITEM_SHARE.equalsIgnoreCase(moreViewIconData.getName())){
            return Utils.getString(mContext, R.string.vec_share_window);
        }else if (BottomContainerView.ViewIconData.TYPE_ITEM_FLAT.equalsIgnoreCase(moreViewIconData.getName())){
            return Utils.getString(mContext, R.string.vec_flat);
        }
        return "";
    }


    public void showStyle(TextView textView, IconTextView oneIcon, BottomContainerView.ViewIconData moreViewIconData) {
        textView.setText(getContent(moreViewIconData));
        oneIcon.setText(moreViewIconData.getDefaultIcon());
        oneIcon.setTextColor(Color.parseColor(moreViewIconData.getDefaultIconColor()));
    }

    public View getItemView() {
        int havMoreView = isHavMoreView();
        View moreItem = null;
        if (havMoreView == 0){
            // moreItem = View.inflate(mContext, R.layout.popup_vec_more_item, null);
        }else if (havMoreView == 1){
            moreItem = View.inflate(mContext, R.layout.popup_vec_more_one_item, null);
            mOneItemFlt = moreItem.findViewById(R.id.oneItemFlt);
            mOneItemFlt.setTag(5);
            mOneItemFlt.setOnClickListener(this);
            TextView oneTextView = moreItem.findViewById(R.id.oneTextView);
            IconTextView oneIcon = moreItem.findViewById(R.id.oneIcon);
            BottomContainerView.ViewIconData moreViewIconData = getMoreViewIconData(0);
            showStyle(oneTextView, oneIcon, moreViewIconData);
        }else if (havMoreView == 2){
            moreItem = View.inflate(mContext, R.layout.popup_vec_more_two_item, null);
            mOneItemFlt = moreItem.findViewById(R.id.oneItemFlt);
            mOneItemFlt.setTag(5);
            mOneItemFlt.setOnClickListener(this);
            mTwoItemFlt = moreItem.findViewById(R.id.twoItemFlt);
            mTwoItemFlt.setTag(6);
            mTwoItemFlt.setOnClickListener(this);
            TextView oneTextView = moreItem.findViewById(R.id.oneTextView);
            mOneIcon = moreItem.findViewById(R.id.oneIcon);
            BottomContainerView.ViewIconData oneViewIconData = getMoreViewIconData(0);
            showStyle(oneTextView, mOneIcon, oneViewIconData);

            BottomContainerView.ViewIconData twoViewIconData = getMoreViewIconData(1);
            mTwoIcon = moreItem.findViewById(R.id.twoIcon);
            TextView twoTextView = moreItem.findViewById(R.id.twoTextView);
            showStyle(twoTextView, mTwoIcon, twoViewIconData);
        }

        return moreItem;
    }

    public void shareWindowAndFlat(String name, boolean isOpen) {
        int index = getIndex(name);
        if (index != -1){
            BottomContainerView.ViewIconData data = mIconDatas.get(index);
            int i = index - 5;
            if (i == 0){
                setIcon(isOpen, mOneIcon, data);
            }else if (i == 1){
                setIcon(isOpen, mTwoIcon, data);
            }
        }
    }

    private void setIcon(boolean isOpen, IconTextView icon, BottomContainerView.ViewIconData data){
        if (icon != null){
            icon.setText(isOpen ? data.getPressIcon() : data.getDefaultIcon());
            icon.setTextColor(isOpen ? Color.parseColor(data.getPressIconColor()) : Color.parseColor(data.getDefaultIconColor()));
        }
    }

    private int getIndex(String name){
        if (mIconDatas == null){
            return -1;
        }
        if (mIconDatas.size() <= 5 ){
            return -1;
        }

        for (int i = 0; i < mIconDatas.size(); i++){
            BottomContainerView.ViewIconData data = mIconDatas.get(i);
            if (data.getName().equalsIgnoreCase(name)){
                return i;
            }
        }

        return -1;
    }


    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        sendImageMessage(selectedImage);
    }

    protected void sendImageMessage(Uri imageUri) {
        com.hyphenate.chat.Message message = com.hyphenate.chat.Message.createImageSendMessage(imageUri, false, AgoraMessage.newAgoraMessage().getCurrentChatUsername());
        if (message != null) {
            // attachMessageAttrs(message);
            ChatClient.getInstance().chatManager().sendMessage(message);
            mMessageList.refreshSelectLastDelay(MessageList.defaultDelay);
        }
    }

    protected void sendTextMessage(String content) {
        if (content.length() == 0){
            return;
        }
        Message message = Message.createTxtSendMessage(content, AgoraMessage.newAgoraMessage().getCurrentChatUsername());
        //attachMessageAttrs(message);
        ChatClient.getInstance().chatManager().sendMessage(message);
        mMessageList.refreshSelectLast();
    }

    private int mNum;
    private boolean mIsShowChatView;
    @Override
    public void onMessage(List<Message> msgs) {
        for (Message message : msgs) {
            String username = message.from();
            // 如果是当前会话的消息，刷新聊天页面
            if (username != null && username.equals(AgoraMessage.newAgoraMessage().getCurrentChatUsername())) {
                mMessageList.refreshSelectLast();
                if (!mIsShowChatView){
                    mNum = mNum + msgs.size();
                    showNum(mNum);
                }
            }
        }
    }

    // 弹出聊天界面
    public void showChatView(){
        mIsShowChatView = true;
        showAndHidden(mLltVecChatView, true);
        showAndHidden(mChatView, true);

        onConversationInit();
        setRefreshLayoutListener();
        // 清空显示数
        if (mBottomContainer != null){
            mNum = 0;
            showNum(0);
        }
    }

    private void showNum(int num){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBottomContainer != null){
                    mBottomContainer.showNum(3, num);
                }
            }
        });
    }

    @Override
    public void onCmdMessage(List<Message> msgs) {

    }

    @Override
    public void onMessageStatusUpdate() {
        mMessageList.refreshSelectLast();
    }

    @Override
    public void onMessageSent() {
        mMessageList.refreshSelectLast();
    }

    /*public void attachMessageAttrs(Message message){
        if (visitorInfo != null){
            message.addContent(visitorInfo);
        }
        if (queueIdentityInfo != null){
            message.addContent(queueIdentityInfo);
        }
        if (agentIdentityInfo != null){
            message.addContent(agentIdentityInfo);
        }
    }*/

    private IVecChatViewCallback mIVecChatViewCallback;
    public interface IVecChatViewCallback{
        void onShowDialog(String content);
        void onHiddenDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void clip(View view){
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                int leftMargin = 0;
                int topMargin = 0;
                Rect selfRect = new Rect(leftMargin, topMargin,
                        rect.right - rect.left - leftMargin,
                        rect.bottom - rect.top - topMargin);
                outline.setRoundRect(selfRect, dp2px(10));
            }
        });
        view.setClipToOutline(true);
    }
    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }

    protected void setListItemClickListener() {
        mMessageList.setItemClickListener(new MessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {

            }

            @Override
            public void onResendClick(final Message message) {
                /*new AlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (!confirmed) {
                            return;
                        }
                        ChatClient.getInstance().chatManager().resendMessage(message);
                    }
                }, true).show();*/
            }

            @Override
            public void onBubbleLongClick(Message message) {

            }

            @Override
            public boolean onBubbleClick(Message message) {
                EMMessageBody body = message.body();
                if (body instanceof EMImageMessageBody){
                    EMImageMessageBody imgBody = (EMImageMessageBody) message.body();
                    Uri imgUri = imgBody.getLocalUri();
                    if (imgUri != null){
                        Log.e("uuuuuuuuuuu","imgUri = "+imgUri);
                        startBlankActivity(BlankActivity.BIG_IMAGE_REQUEST, imgUri, message.messageId(), imgBody.getFileName());
                        return true;
                    }
                }else if (body instanceof EMTextMessageBody){
                    // TODO 点击文本消息
                    Log.e("uuuuuuuuuuu","body = "+((EMTextMessageBody) body).getMessage());
                    Log.e("uuuuuuuuuuu","getType = "+message.getType());
                }

                return false;
            }

            @Override
            public void onMessageItemClick(Message message, MessageList.ItemAction action) {

            }
        });
    }

    public static final String TYPE_BLANK_KEY = "type_blank_key";
    public static final String TYPE_BLANK_PARCELABLE_KEY = "type_blank_parcelable_key";
    public static final String TYPE_BLANK_MSG_ID_key = "type_blank_parcelable_message_id_key";
    public static final String TYPE_BLANK_FILE_NAME_KEY = "type_blank_parcelable_message_file_name_key";

    private void startBlankActivity(int type, Parcelable parcelable, String msgId, String fileName){
        Intent intent = new Intent(mContext, BlankActivity.class);
        intent.putExtra(TYPE_BLANK_KEY,type);
        if (parcelable != null){
            intent.putExtra(TYPE_BLANK_PARCELABLE_KEY, parcelable);
        }

        if (!TextUtils.isEmpty(msgId)){
            intent.putExtra(TYPE_BLANK_MSG_ID_key, msgId);
        }

        if (!TextUtils.isEmpty(fileName)){
            intent.putExtra(TYPE_BLANK_FILE_NAME_KEY, msgId);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        showAndHidden(mShowView, true);
        if (BlankActivity.IMAGE_REQUEST == requestCode){
            // 选择图库返回路径
            if (data != null){
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    sendPicByUri(selectedImage);
                }
            }
        }
    }
}


