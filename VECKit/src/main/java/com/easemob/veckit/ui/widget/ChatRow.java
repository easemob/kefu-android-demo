package com.easemob.veckit.ui.widget;


import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.veckit.R;
import com.easemob.veckit.ui.widget.utils.ToastHelper;
import com.easemob.veckit.ui.widget.utils.UIProvider;
import com.easemob.veckit.ui.widget.utils.UserUtil;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.DateUtils;

import java.util.Date;

public abstract class ChatRow extends LinearLayout {

    protected static final String TAG = ChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected Message message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Context activity;
    private Handler mHandler = new Handler();

//    protected TextView ackedView;
//    protected TextView deliveredView;

    protected Callback messageSendCallback;
    protected Callback messageReceiveCallback;

    protected MessageList.MessageListItemClickListener itemClickListener;

    public ChatRow(Context context, Message message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.activity =  context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflatView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
//        ackedView = (TextView) findViewById(R.id.tv_ack);
//        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * 根据当前message和position设置控件属性等
     *
     * @param message
     * @param position
     */
    public void setUpView(Message message, int position,
                          MessageList.MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.messageTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                Message prevMessage = (Message) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.messageTime(), prevMessage.messageTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.messageTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //设置头像和nick

        UIProvider.UserProfileProvider userInfoProvider = UIProvider.getInstance().getUserProfileProvider();

        if (userInfoProvider != null) {
            userInfoProvider.setNickAndAvatar(context, message, userAvatarView, usernickView);
        }else{
            if (message.direct() == Message.Direct.RECEIVE) {
                if (usernickView != null){
                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                }
            } else {
                UserUtil.setCurrentUserNickAndAvatar(context, userAvatarView, usernickView);
            }
        }


        if (adapter instanceof MessageAdapter) {
            if (userAvatarView != null){
                if (((MessageAdapter) adapter).isShowAvatar() && message.direct() == Message.Direct.RECEIVE){
                    userAvatarView.setVisibility(View.VISIBLE);
                }
                else{
                    userAvatarView.setVisibility(View.GONE);
                }
            }


            if (usernickView != null) {
                if (((MessageAdapter) adapter).isShowUserNick() && message.direct() == Message.Direct.RECEIVE)
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (bubbleLayout != null) {
                if (message.direct() == Message.Direct.SEND) {
                    if (((MessageAdapter) adapter).getMyBubbleBg() != null)
                        bubbleLayout.setBackgroundDrawable(((MessageAdapter) adapter).getMyBubbleBg());
                } else if (message.direct() == Message.Direct.RECEIVE) {
                    if (((MessageAdapter) adapter).getOtherBuddleBg() != null)
                        bubbleLayout.setBackgroundDrawable(((MessageAdapter) adapter).getOtherBuddleBg());
                }

            }

        }
    }

    /**
     * 设置消息发送callback
     */
    protected void setMessageSendCallback() {
        if (messageSendCallback == null && message.messageStatusCallback() == null) {
            messageSendCallback = new Callback() {
                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onError(int i, String s) {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (percentageView != null && progress < 100) {
                                percentageView.setTag(progress);
                                percentageView.setText(progress + "%");
                            }

                        }
                    });

                }
            };

        }
        message.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * 设置消息接收callback
     */
    protected void setMessageReceiveCallback() {
        Log.e(TAG,"messageReceiveCallback = "+messageReceiveCallback);
        if (messageReceiveCallback == null) {
            messageReceiveCallback = new Callback() {
                @Override
                public void onSuccess() {
                    Log.e(TAG,"onSuccess");
                    updateView();
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(TAG,"error = "+s+", code = "+i);
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String s) {
                    Log.e(TAG,"onProgress = "+s);
                    mHandler.post(new Runnable() {
                        public void run() {
                            if (percentageView != null && progress < 100) {
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }


    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (!itemClickListener.onBubbleClick(message)) {
                            //如果listener返回false不处理这个事件，执行lib默认的处理
                            onBubbleClick();
                        }
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Message.Direct.SEND) {
                            itemClickListener.onUserAvatarClick(ChatClient.getInstance().currentUserName());
                        } else {
                            itemClickListener.onUserAvatarClick(message.from());
                        }
                    }
                }
            });
        }

    }


    protected void updateView() {
        mHandler.post(new Runnable() {
            public void run() {
                if (message.status() == Message.Status.FAIL) {
                    if (message.error() == Error.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                        ToastHelper.show(activity, R.string.vec_send_fail);
                    } else {
                        ToastHelper.show(activity, activity.getString(R.string.vec_send_fail) + activity.getString(R.string.connect_failuer_toast));
                    }
                }

                onUpdateView();
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        message.setMessageStatusCallback(null);
    }

    /**
     * 填充layout
     */
    protected abstract void onInflatView();

    /**
     * 查找chatrow里的控件
     */
    protected abstract void onFindViewById();

    /**
     * 消息状态改变，刷新listview
     */
    protected abstract void onUpdateView();

    /**
     * 设置更新控件属性
     */
    protected abstract void onSetUpView();

    /**
     * 聊天气泡被点击事件
     */
    protected abstract void onBubbleClick();

}