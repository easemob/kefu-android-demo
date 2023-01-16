package com.easemob.veckit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.easemob.veckit.ui.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.easemob.veckit.bean.DegreeBean;
import com.easemob.veckit.bean.EnquiryOptionsBean;
import com.easemob.veckit.bean.EntityBean;
import com.easemob.veckit.bean.SubmitEvaluationBean;
import com.easemob.veckit.bean.VideoStyleBean;
import com.easemob.veckit.floating.FloatWindowManager;
import com.easemob.veckit.ui.EvaluateView;
import com.easemob.veckit.ui.flow.FlowBean;
import com.easemob.veckit.ui.flow.FlowTagLayout;
import com.easemob.veckit.utils.FlatFunctionUtils;
import com.easemob.veckit.utils.Utils;
import com.easemob.veckit.utils.WaitNetworkUtils;
import com.google.gson.Gson;
import com.hyphenate.agora.FunctionIconItem;
import com.hyphenate.agora.IEndCallback;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.VecConfig;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CallVideoActivity extends BaseActivity implements View.OnClickListener, IEndCallback, RatingBar.OnRatingChangeListener {
    private final static String TAG = "VECVideo";
    public final static String DIALOG_TYPE_KEY = "dialog_type_key";
    public final static String LOAD_LOCAL_STYLE = "load_local_style";
    // 数据key
    public final static String VIDEO_STYLE_KEY = "video_style_key";
    private final static String JSON_KEY = "json_key_%s";
    private final static int CLOSE_CALL_TIMEOUT = 20 * 60 * 1000;// 未接听，1分钟后超时关闭
    // 无，被动请求
    public final static int DIALOG_TYPE_NO = 0;
    // 发起视频之前
    public final static int DIALOG_TYPE_DEFAULT = 1;
    // 开始发起视频
    public final static int DIALOG_TYPE_SEND = 2;
    // 发起视频，等待人数
    public final static int DIALOG_TYPE_WAIT = 3;
    // 满意度
    public final static int DIALOG_TYPE_RETRY = 4;
    // 座席端发起视频
    public final static int DIALOG_TYPE_PASSIVE = 5;
    // 挂断
    public final static int DIALOG_TYPE_END = 6;
    // 当前dialog类型
    private int mCurrentDialogType;

    // 派对超时，通话结束
    private TextView mTimeoutTv;

    private TextView mNameTv;
    private TextView mContentTv;
    private ImageView mTypeIv;
    private TextView mTypeTv;
    private View mCloseTv;
    private String mToChatUserName;
    private VideoStyleBean mVideoStyleBean;
    private SharedPreferences mSharedPreferences;
    private static String sToChatUserName;
    private boolean mIsCreate;
    private boolean mIsHavPermission;
    private boolean mClickRequestPermission;
    private View mContent;
    private WindowManager mWm;
    private Point mPoint;
    private int mNavHeight;
    private ImageView mWaitingIV;
    private ImageView mCallingIV;
    private ImageView mQueuingIV;
    private ImageView mEndingIV;
    private ImageView mHangupIv;
    private View mPassiveLlt;
    private ImageView mAcceptIv;
    private String mSmg;
    private EvaluateView mEvaluateFlt;
    private TextView mEvaluateTv;
    private View mOkEvaluateTv;
    private FlowTagLayout mFlowTagLayout;
    private RatingBar mRatingBar;
    private View mProgressTv;
    private TextView mTitleTv;
    private TextView mShowTv;
    private EditText mEtView;
    private boolean mIsRun;
    private ImageView mPhotoIv;

    // 主动
    public static void callingRequest(Context context, String toChatUserName) {
        Intent intent = new Intent(context, CallVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_DEFAULT);
        // 主动
        intent.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        if (TextUtils.isEmpty(toChatUserName)) {
            toChatUserName = AgoraMessage.newAgoraMessage().getCurrentChatUsername();
        }
        EMLog.e("VECVideo","主动 CallVideoActivity.callingRequest toChatUserName = "
                +toChatUserName);
        intent.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, toChatUserName);
        context.startActivity(intent);
    }

    // 主动
    public static void callingRequest(Context context, String toChatUserName, String jsonStyle) {
        sToChatUserName = toChatUserName;
        Intent intent = new Intent(context, CallVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_DEFAULT);
        // 主动
        intent.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        intent.putExtra(VIDEO_STYLE_KEY, jsonStyle);
        if (TextUtils.isEmpty(toChatUserName)) {
            toChatUserName = AgoraMessage.newAgoraMessage().getCurrentChatUsername();
        }
        intent.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, toChatUserName);
        EMLog.e("VECVideo","主动 CallVideoActivity.callingRequest toChatUserName = "
                +toChatUserName+", jsonStyle = "+jsonStyle);
        context.startActivity(intent);
    }

    public static void startDialogTypeRetry(Context context, String content) {
        Intent intent = new Intent(context, CallVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_RETRY);
        intent.putExtra(LOAD_LOCAL_STYLE, true);
        // 主动
        intent.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    // 挂断电话显示的页面
    public static void startDialogTypeEnd(Context context, String toChatUserName) {
        Intent intent = new Intent(context, CallVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_END);
        intent.putExtra(LOAD_LOCAL_STYLE, true);
        // 主动
        intent.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        if (TextUtils.isEmpty(toChatUserName)) {
            toChatUserName = AgoraMessage.newAgoraMessage().getCurrentChatUsername();
        }
        intent.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, toChatUserName);
        context.startActivity(intent);
    }

    /*public static void startDialogTypeRetry(Context context, String toChatUserName) {
        Intent intent = new Intent(context, CallVideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_RETRY);
        intent.putExtra(LOAD_LOCAL_STYLE, true);
        // 主动
        intent.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        if (TextUtils.isEmpty(toChatUserName)) {
            toChatUserName = AgoraMessage.newAgoraMessage().getCurrentChatUsername();
        }
        intent.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, toChatUserName);
        context.startActivity(intent);
    }*/


    // 被动
    public static void callingResponse(Context context, Intent intent) {

        int zuo_xi_active = intent.getIntExtra("zuo_xi_active", 0);
        Intent i = new Intent(context, CallVideoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_NO);

        i.putExtra("to", intent.getStringExtra("to"));
        i.putExtra("from", intent.getStringExtra("from"));
        i.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, intent.getStringExtra("from"));

        if (zuo_xi_active == 0){
            i.putExtra("type", intent.getStringExtra("type"));
            i.putExtra("appid", intent.getStringExtra("appid"));
            Parcelable zuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");
            i.putExtra("zuoXiSendRequestObj", zuoXiSendRequestObj);

            // 被动
            i.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_PASSIVE_VALUE);
            EMLog.e("VECVideo","访客主动发送视频邀请，坐席响应访客视频邀请 CallVideoActivity.callingResponse");
        }else {
            // 坐席主动发送邀请
            i.putExtra("msg", intent.getStringExtra("msg"));
            i.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_ZUO_XI_ACTIVE_VALUE);
            EMLog.e("VECVideo","坐席主动发送视频邀请 CallVideoActivity.callingResponse");
        }

        context.startActivity(i);

    }

    // 被动
    public static void callingResponse(Context context, Intent intent, String jsonStyle) {
        Intent i = new Intent(context, CallVideoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_NO);
        intent.putExtra(VIDEO_STYLE_KEY, jsonStyle);

        i.putExtra("type", intent.getStringExtra("type"));
        i.putExtra("appid", intent.getStringExtra("appid"));
        Parcelable zuoXiSendRequestObj = intent.getParcelableExtra("zuoXiSendRequestObj");
        i.putExtra("zuoXiSendRequestObj", zuoXiSendRequestObj);
        i.putExtra("to", intent.getStringExtra("to"));
        i.putExtra("from", intent.getStringExtra("from"));

        // 被动
        i.putExtra(VideoCallWindowService.INTENT_CALLING_TAG, VideoCallWindowService.INTENT_CALLING_TAG_PASSIVE_VALUE);
        i.putExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME, intent.getStringExtra("from"));
        context.startActivity(i);

    }

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mNavHeight = getNav(mWm, mContent, mPoint);
            mContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.activity_vec_call_video;
    }

    @Override
    public void handleMessage(Message msg) {
        dialogType(msg.what);
    }

    @Override
    public boolean isLoadLayoutRes(Intent intent) {
        // 判断是否为主动
        int isActive = intent.getIntExtra(VideoCallWindowService.INTENT_CALLING_TAG,
                VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);



        return isActive == VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE
                || isActive == VideoCallWindowService.INTENT_CALLING_TAG_ZUO_XI_ACTIVE_VALUE;
    }

    @Override
    public void initView(@NonNull Intent intent, @Nullable Bundle savedInstanceState) {
        AgoraMessage.newAgoraMessage().registerIEndCallback(getClass().getSimpleName(), this);
        mSharedPreferences = getSharedPreferences("video_style", MODE_PRIVATE);
        // 判断是否为主动
        int isActive = intent.getIntExtra(VideoCallWindowService.INTENT_CALLING_TAG,
                VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        try {
            initEvaluate();
            initPassiveView();
            initStyle(intent, isActive == VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        checkPermission();
        if (isActive == VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE) {
            mToChatUserName = intent.getStringExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME);
            initView();
            int dialogType = intent.getIntExtra(DIALOG_TYPE_KEY, DIALOG_TYPE_DEFAULT);
            if (dialogType == DIALOG_TYPE_DEFAULT) {
                /*dialogType(mVideoStyleBean.getFunctionSettings().isSkipWaitingPage() ? DIALOG_TYPE_SEND : DIALOG_TYPE_DEFAULT);
                if (mCurrentDialogType == DIALOG_TYPE_SEND) {
                    activeVideo(mIsHavPermission);
                }*/

                dialogType(mVideoStyleBean.getFunctionSettings().isSkipWaitingPage() ? DIALOG_TYPE_WAIT : DIALOG_TYPE_DEFAULT);
                if (mCurrentDialogType == DIALOG_TYPE_WAIT) {
                    activeVideo(mIsHavPermission);
                    // requestWait();
                }

            } else if (dialogType == DIALOG_TYPE_SEND) {
                dialogType(DIALOG_TYPE_SEND);
            } else if (dialogType == DIALOG_TYPE_RETRY) {
                // 满意度评价
                mEvaluateFlt.setIsAllowClick(true);
                dialogType(DIALOG_TYPE_RETRY);
            }else if (dialogType == DIALOG_TYPE_END){
                // 挂断后显示的页面
                dialogType(DIALOG_TYPE_END);
            }
        } else if (isActive == VideoCallWindowService.INTENT_CALLING_TAG_ZUO_XI_ACTIVE_VALUE){
            // 坐席主动邀请视频
            mToChatUserName = intent.getStringExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME);
            mSmg = intent.getStringExtra("msg");
            getTenantIdFunctionIcons();
            initView();
            dialogType(DIALOG_TYPE_NO);
        }else {
            // 被动 坐席 --> 访客端 响应
            // 检测是否有悬浮权限
            passVideo(FloatWindowManager.getInstance().checkPermission(this), intent);
        }
    }

    // 初始化评价相关控件
    private void initEvaluate() {
        // 排队超时，通话结束
        mTimeoutTv = $(R.id.timeoutTv);

        mEvaluateFlt = $(R.id.evaluateFlt);
        mFlowTagLayout = $(R.id.flowTagLayout);
        mRatingBar = $(R.id.ratingBar);
        // title文字
        mTitleTv = $(R.id.titleTv);
        // 备注
        mEtView = $(R.id.etView);
        // mRatingBar.setOnRatingBarChangeListener(this);
        mRatingBar.setOnRatingChangeListener(this);
        // 满意度文字
        mShowTv = $(R.id.showTv);
        // 评价完成页面
        mEvaluateTv = $(R.id.evaluateTv);
        mProgressTv = $(R.id.progressTv);
        mOkEvaluateTv = $(R.id.okEvaluateTv);
        mOkEvaluateTv.setOnClickListener(this);
        View evaluateView = $(R.id.evaluateView);
        clipToOutline(evaluateView);
    }

    // 坐席主动发视频邀请
    private void initPassiveView() {
        mPassiveLlt = $(R.id.passiveLlt);
        mHangupIv = $(R.id.hangupIv);
        mAcceptIv = $(R.id.acceptIv);
        mHangupIv.setOnClickListener(this);
        mAcceptIv.setOnClickListener(this);
    }

    private void request(Callback callback) {
        String tenantId = ChatClient.getInstance().tenantId();// "77556"
        String configId = ChatClient.getInstance().getConfigId();
        if (TextUtils.isEmpty(configId)) {
            return;
        }
        AgoraMessage.asyncInitStyle(tenantId, configId, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    if (!jsonObject.has("status")) {
                        return;
                    }
                    String status = jsonObject.getString("status");
                    if (!"ok".equalsIgnoreCase(status)) {
                        return;
                    }

                    // 存起来
                    JSONObject entity = jsonObject.getJSONObject("entity");
                    if (isFinishing()) {
                        return;
                    }

                    String json = entity.toString();
                    if (!getLocalData().equals(json)) {
                        // 改变数据
                        runOnUiThread(() -> {
                            try {
                                // 保存本地
                                saveLocalData(json);
                                callback.run(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }

    private void saveLocalData(/*JSONObject entity*/String entity) {
        // 保存本地
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(String.format(JSON_KEY, sToChatUserName), entity);
        edit.apply();
    }

    private String getLocalData() {
        return mSharedPreferences.getString(String.format(JSON_KEY, sToChatUserName), "");
    }

    private int getNav(WindowManager wm, View content, Point point) {
        Display display = wm.getDefaultDisplay();
        display.getRealSize(point);
        if (content.getBottom() == 0) {
            return 0;
        }
        return point.y - content.getBottom();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        EMLog.e(TAG, "onNewIntent");

        int dialogType = intent.getIntExtra(DIALOG_TYPE_KEY, -1);
        if (dialogType == DIALOG_TYPE_RETRY){
            // 显示满意度
            mCurrentDialogType = DIALOG_TYPE_RETRY;
            // mEvaluateFlt.setIsAllowClick(false);
            mEvaluateFlt.setIsAllowClick(true);
            initRetry(intent);
            return;
        }


        int isActive = intent.getIntExtra(VideoCallWindowService.INTENT_CALLING_TAG,
                VideoCallWindowService.INTENT_CALLING_TAG_ACTIVE_VALUE);

        if (mCurrentDialogType == DIALOG_TYPE_SEND || mCurrentDialogType == DIALOG_TYPE_WAIT) {
            if (isActive == VideoCallWindowService.INTENT_CALLING_TAG_PASSIVE_VALUE) {
                String toChatUserName = intent.getStringExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME);
                if (!TextUtils.isEmpty(toChatUserName)
                        && !TextUtils.isEmpty(mToChatUserName)
                        && mToChatUserName.equals(toChatUserName)) {
                    if (!mIsCreate) {
                        mIsCreate = true;
                        activeVideoResponse(mIsHavPermission, intent);
                    }
                }
            }
        }else {
            // 被动 正在显示默认或重新发送页面，坐席端发送过来请求
            if (isActive == VideoCallWindowService.INTENT_CALLING_TAG_PASSIVE_VALUE) {
                /*if (!mIsCreate){
                    mIsCreate = true;
                    passVideo(FloatWindowManager.getInstance().checkPermission(this), intent);
                }*/

                /*String toChatUserName = intent.getStringExtra(VideoCallWindowService.CURRENT_CHAT_USER_NAME);
                if (!TextUtils.isEmpty(toChatUserName)
                        && !TextUtils.isEmpty(mToChatUserName)
                        && mToChatUserName.equals(toChatUserName)) {
                    if (!mIsCreate) {
                        mIsCreate = true;
                        activeVideoResponse(mIsHavPermission, intent);
                    }
                }
                if (!mIsCreate) {
                    mIsCreate = true;
                    activeVideoResponse(mIsHavPermission, intent);
                }*/

                activeVideoResponse(mIsHavPermission, intent);

            }else if (isActive == VideoCallWindowService.INTENT_CALLING_TAG_ZUO_XI_ACTIVE_VALUE){
                // 坐席主动发送邀请
                if (!mIsCreate) {
                    mIsCreate = true;

                    mSmg = intent.getStringExtra("msg");
                    initView();
                    dialogType(DIALOG_TYPE_NO);
                }
            }
        }
    }

    // 被动发起视频
    private void passVideo(boolean checkPermission, Intent intent) {
        EMLog.e(TAG, "被动 正在通话 座席端 -- 访客端");

        if (checkPermission) {
            intent.putExtra("nav_height", mNavHeight);
            VideoCallWindowService.show(this, intent);
        } else {
            CallActivity.show(this, intent);
        }

        finishPage();
    }

    private void activeVideo(boolean checkPermission) {
        EMLog.e(TAG, "主动发起请求 是否有悬浮权限 = " + checkPermission);
        VecConfig.newVecConfig().setVecVideo(true);
        sendCmd();
        startTimerOut();
    }

    private void activeVideoResponse(boolean isHavPermission, Intent intent) {
        EMLog.e(TAG, "主动发起请求 获取到座席端响应");
        stopTimerOut();
        if (isHavPermission) {
            intent.putExtra("nav_height", mNavHeight);
            VideoCallWindowService.show(this, mToChatUserName, intent);
        } else {
            CallActivity.show(this, mToChatUserName, intent);
        }

        finishPage();
    }

    // 发送请求建立视频
    private void sendCmd() {
        mIsRun = true;
        EMLog.e(TAG, "发送请求建立视频 sendCmd");
        ChatClient.getInstance().callManager().callVecVideo(Utils.getString(getApplicationContext(), R.string.vec_agent_to_visitor), mToChatUserName);
        EMLog.e(TAG, "发送请求建立视频 sendCmd 结束");
    }

    private Runnable mCloseTimerOut;

    private void startTimerOut() {
        if (mCloseTimerOut == null) {
            mCloseTimerOut = () -> {
                Log.e("aaaaaaaaaa", "挂断");
                // 超时拒接
                EMLog.e(TAG, "主动发起请求，坐席端超时，挂断");
                mIsCreate = false;
                mIsRun = false;
                // ChatClient.getInstance().callManager().endCall(0, true);
                VECKitCalling.endCallFromOff();
                VecConfig.newVecConfig().setVecVideo(false);
                // 回复状态
                dialogType(DIALOG_TYPE_DEFAULT);
            };
        }
        removeRunnable(mCloseTimerOut);
        postDelayed(mCloseTimerOut, CLOSE_CALL_TIMEOUT);
    }

    private void stopTimerOut() {
        if (mCloseTimerOut != null) {
            removeRunnable(mCloseTimerOut);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.typeIv) {
            // 发起视频之前
            if (DIALOG_TYPE_DEFAULT == mCurrentDialogType) {
                /*dialogType(DIALOG_TYPE_SEND);
                activeVideo(mIsHavPermission);*/

                dialogType(DIALOG_TYPE_WAIT);
                activeVideo(mIsHavPermission);
                // requestWait();
            } else if (DIALOG_TYPE_RETRY == mCurrentDialogType) {
                // 满意度（特殊情况，会话结束，用户瞬间又点击发起通话，这时满意度页面通知还没过来，当满意度页面通知过来时，就会将状态改变为DIALOG_TYPE_RETRY，其实正在接通排队）
                // 排队等待时，挂断：分两种情况，1.正在排队挂断。2.待接入时，挂断
                mIsCreate = false;
                mIsRun = false;
                VECKitCalling.endCallFromOff();
                // finishPage();
                dialogType(DIALOG_TYPE_END);
            } else if (DIALOG_TYPE_WAIT == mCurrentDialogType) {
                Log.e(TAG, "wait");
                // 排队等待时，挂断：分两种情况，1.正在排队挂断。2.待接入时，挂断
                mIsCreate = false;
                mIsRun = false;
                VECKitCalling.endCallFromOff();
                // finishPage();
                dialogType(DIALOG_TYPE_END);

            } else if (DIALOG_TYPE_SEND == mCurrentDialogType) {
                // 挂断
                // ChatClient.getInstance().callManager().endVecCall(0, true);
                VECKitCalling.endCallFromOff();
                mIsCreate = false;
                mIsRun = false;
                // finishPage();
                dialogType(DIALOG_TYPE_END);

            }else if (DIALOG_TYPE_END == mCurrentDialogType){
                // 重新发起
                /*dialogType(DIALOG_TYPE_SEND);
                activeVideo(mIsHavPermission);*/
                dialogType(DIALOG_TYPE_WAIT);
                activeVideo(mIsHavPermission);
                // requestWait();
            }
        } else if (id == R.id.closeTv) {
            if (mIsRun){
                return;
            }
            // 关闭
            clear();
            finish();
        } else if (id == R.id.hangupIv) {
            // 坐席主动发视频邀请，拒接按钮
            VECKitCalling.endCallFromZuoXi(Utils.getString(getApplicationContext(), R.string.vec_visitor_refuse_video));
            // 关闭
            clear();
            finish();

        } else if (id == R.id.acceptIv) {
            // 坐席主动发视频邀请，接听按钮
            VECKitCalling.acceptCallFromZuoXi(Utils.getString(getApplicationContext(), R.string.vec_visitor_accept_video));
        }else if (id == R.id.okEvaluateTv){
            // 提交满意度评价
            submitEvaluate();
        }

    }

    private void showProgressTv(){
        if (mProgressTv != null){
            showAndHidden(mProgressTv,true);
        }
    }

    private void hiddenProgressTv(){
        if (mProgressTv != null){
            showAndHidden(mProgressTv,false);
        }
    }



    // 满意度提交完成
    private void evaluateOk() {
        if (!mIsRun){
            mCurrentDialogType = DIALOG_TYPE_DEFAULT;
        }

        mEvaluateFlt.setIsAllowClick(true);
        /*if (!mIsRun){
            showAndHidden(mCloseTv, true);
        }*/
        showAndHidden(mEvaluateTv, true);
        // 3s 之后自动关闭
        postDelayed(() -> {
            if (isFinishing()){
                return;
            }

            showAndHidden(mEvaluateFlt, false);
            /*clear();
            finish();*/
        }, 3000);
    }

    // 满意度提交失败
    private void evaluateError() {
        showToast(Utils.getString(getApplicationContext(), R.string.vec_request_fail));
        mCurrentDialogType = DIALOG_TYPE_DEFAULT;
        mEvaluateFlt.setIsAllowClick(true);
        showAndHidden(mCloseTv, true);
        showAndHidden(mEvaluateTv, false);
    }

    private void checkPermission() {
        if (!FloatWindowManager.getInstance().checkPermission(this)) {
            mIsHavPermission = false;
            FloatWindowManager.getInstance().applyPermission(this, confirm -> mClickRequestPermission = confirm);
        } else {
            mIsHavPermission = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mClickRequestPermission) {
            mIsHavPermission = FloatWindowManager.getInstance().checkPermission(this);
        }
    }

    private void dialogType(int type) {
        mCurrentDialogType = type;
        if (type == DIALOG_TYPE_DEFAULT) {
            // 发起视频之前
            showBackground(mWaitingIV);
            dialogTypeDefault();
        } else if (type == DIALOG_TYPE_SEND) {
            // 开始发起视频
            showBackground(mCallingIV);
            dialogTypeSend();
        } else if (type == DIALOG_TYPE_WAIT) {
            // 发起视频，等待人数
            showBackground(mQueuingIV);
            dialogTypeWait();
        } else if (type == DIALOG_TYPE_RETRY) {
            // 满意度评价
            showBackground(mEndingIV);
            dialogTypeRetry();
            initRetry(getIntent());
        }else if (type == DIALOG_TYPE_END){
            // 挂断之后显示的页面
            showBackground(mEndingIV);
            dialogTypeEnd();
        }else if(type == DIALOG_TYPE_NO){
            // 默认坐席主动发送视频邀请
            showBackground(mCallingIV);
            dialogTypeNo();
        }
    }

    private void showBackground(ImageView imageView) {
        showAndHidden(mWaitingIV, mWaitingIV == imageView);
        showAndHidden(mCallingIV, mCallingIV == imageView);
        showAndHidden(mEndingIV, mEndingIV == imageView);
        showAndHidden(mQueuingIV, mQueuingIV == imageView);
    }

    private void netWork() {
        request(json -> {
            EntityBean entityBean = new EntityBean(json);
            mVideoStyleBean = entityBean.getVideoStyleBean(getApplicationContext());
            changeBackgroundImage();
        });
    }

    private void changeBackgroundImage() {
        String path = getCacheDir().toString();
        VecConfig.newVecConfig().setCameraState(mVideoStyleBean.getFunctionSettings().isVisitorCameraOff());
        loadImage(mWaitingIV, path, mVideoStyleBean.getStyleSettings().getWaitingBackgroundPic());
        loadImage(mCallingIV, path, mVideoStyleBean.getStyleSettings().getCallingBackgroundPic());
        loadImage(mQueuingIV, path, mVideoStyleBean.getStyleSettings().getQueuingBackgroundPic());
        loadImage(mEndingIV, path, mVideoStyleBean.getStyleSettings().getEndingBackgroundPic());
    }

    private void loadImage(ImageView imageView, String saveLocalPath, String url) {
        if (imageView == null) {
            return;
        }

        if (!TextUtils.isEmpty(url)) {
            saveLocalPath = saveLocalPath.concat("/vec");
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            File file = new File(saveLocalPath, fileName);
            if (file.exists()) {
                // 加载本地图片
                if (isFinishing()) {
                    return;
                }
                /*Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                bitmap.recycle();*/
                imageView.setImageURI(Uri.fromFile(file));
            } else {
                File search = new File(saveLocalPath);
                if (!search.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    search.mkdirs();
                }

                if (isFinishing()) {
                    return;
                }
                // 请求网络，保存本地图片
                NetWork.loadImage(file.getPath(), url, new NetWork.CallBack() {
                    @Override
                    public void ok(String url) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(() -> imageView.setImageURI(Uri.fromFile(new File(url))));
                    }

                    @Override
                    public void fail(int code, String error) {
                    }
                });
            }
        } else {
            // imageView.setImageResource(R.drawable.dialog_corners_bg);
            imageView.setBackgroundColor(getResources().getColor(R.color.dialog_corners_bg));
        }
    }

    private void loadAvatarImage(ImageView imageView, String saveLocalPath, String url) {
        if (imageView == null) {
            return;
        }

        if (!TextUtils.isEmpty(url)) {
            saveLocalPath = saveLocalPath.concat("/vec");
            File file = new File(saveLocalPath, String.format("avatar_%s",ChatClient.getInstance().tenantId()));
            if (file.exists()) {
                // 加载本地图片
                if (isFinishing()) {
                    return;
                }
                /*Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                bitmap.recycle();*/
                imageView.setImageURI(Uri.fromFile(file));
            } else {
                File search = new File(saveLocalPath);
                if (!search.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    search.mkdirs();
                }

                if (isFinishing()) {
                    return;
                }
                // 请求网络，保存本地图片
                NetWork.loadImage(file.getPath(), url, new NetWork.CallBack() {
                    @Override
                    public void ok(String url) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(() -> imageView.setImageURI(Uri.fromFile(new File(url))));
                    }

                    @Override
                    public void fail(int code, String error) {
                    }
                });
            }
        }
    }

    private void initStyle(@NonNull Intent intent, boolean isActive) throws JSONException {
        // 背景视图倒角
        /*if (isActive) {
            mWaitingIV = $(R.id.waitingIV);
            clipToOutline(mWaitingIV);
            mCallingIV = $(R.id.callingIV);
            clipToOutline(mCallingIV);
            mQueuingIV = $(R.id.queuingIV);
            clipToOutline(mQueuingIV);
            mEndingIV = $(R.id.endingIV);
            clipToOutline(mEndingIV);
        }*/

        boolean loadLocalStyle = intent.getBooleanExtra(LOAD_LOCAL_STYLE, false);

        // 本地取值
        String localData = getLocalData();
        if (!TextUtils.isEmpty(localData)) {
            EntityBean entityBean = new EntityBean(localData);
            mVideoStyleBean = entityBean.getVideoStyleBean(getApplicationContext());
            changeBackgroundImage();
        } else {
            mVideoStyleBean = VideoStyleBean.create(getApplicationContext());
        }
        String jsonStyle = intent.getStringExtra(VIDEO_STYLE_KEY);
        Log.e("aaaaaaaaaaaa", "传递值 = " + jsonStyle);
        if (!TextUtils.isEmpty(jsonStyle)) {
            initStyleFromIntent(jsonStyle);
        } else {
            if (!loadLocalStyle) {
                netWork();
            } else {
                Log.e("aaaaaaaaaa", "加载本地样式");
            }
        }
    }

    private void clipToOutline(View surfaceView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            surfaceView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    int leftMargin = 0;
                    int topMargin = 0;
                    Rect selfRect = new Rect(leftMargin, topMargin,
                            rect.right - rect.left - leftMargin,
                            rect.bottom - rect.top - topMargin);
                    outline.setRoundRect(selfRect, dp2px(8));
                }
            });
            surfaceView.setClipToOutline(true);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private void initStyleFromIntent(String jsonStyle) {
        try {
            // 整体数据
            if (!getLocalData().equals(jsonStyle)) {
                // 改变数据
                saveLocalData(jsonStyle);
                EntityBean entityBean = new EntityBean(jsonStyle);
                mVideoStyleBean = entityBean.getVideoStyleBean(getApplicationContext());
                changeBackgroundImage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        mPhotoIv = $(R.id.photoIv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clip(mPhotoIv, 50);
        }
        // 头像
        mPhotoIv.setBackgroundResource(R.drawable.wait_icon);
        loadAvatarImage(mPhotoIv, getCacheDir().toString(), VecConfig.newVecConfig().getAvatarImage());

        mCloseTv = $(R.id.closeTv);
        mNameTv = $(R.id.nameTv);
        mContentTv = $(R.id.contentTv);
        mTypeIv = $(R.id.typeIv);
        mTypeTv = $(R.id.typeTv);
        mCloseTv.setOnClickListener(this);
        mTypeIv.setOnClickListener(this);

        mContent = getWindow().getDecorView().findViewById(android.R.id.content);
        mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mPoint = new Point();
        mContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void dialogTypeRetry() {
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHidden(mCloseTv, true);
        // mNameTv.setText("环信");
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(mVideoStyleBean.getStyleSettings().getEndingPrompt());
        mTypeIv.setImageResource(R.drawable.em_icon_call_accept);
        mTypeTv.setText(Utils.getString(getApplicationContext(), R.string.vec_satisfaction_evaluation));
    }

    private void dialogTypeEnd() {
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHidden(mCloseTv, true);
        // mNameTv.setText("环信");
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(mVideoStyleBean.getStyleSettings().getEndingPrompt());
        mTypeIv.setImageResource(R.drawable.em_icon_call_accept);
        // 重新发起
        mTypeTv.setText(getResources().getString(R.string.vec_again_calling));
    }

    private void dialogTypeWait() {
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHiddenInvisible(mCloseTv, false);
        // mNameTv.setText("客服花花");
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(mVideoStyleBean.getStyleSettings().getQueuingPrompt());
        mTypeIv.setImageResource(R.drawable.em_icon_call_hangup);
        // 挂断
        mTypeTv.setText(getResources().getString(R.string.vec_hang_up));
    }

    private void dialogTypeWaitUpdateUi(String content){
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHiddenInvisible(mCloseTv, false);
        // mNameTv.setText("客服花花");
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(content);
        mTypeIv.setImageResource(R.drawable.em_icon_call_hangup);
        // 挂断
        mTypeTv.setText(getResources().getString(R.string.vec_hang_up));
    }

    private void dialogTypeSend() {
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHiddenInvisible(mCloseTv, false);
        // mNameTv.setText("环信");
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(mVideoStyleBean.getStyleSettings().getCallingPrompt());
        mTypeIv.setImageResource(R.drawable.em_icon_call_hangup);
        // 挂断
        mTypeTv.setText(getResources().getString(R.string.vec_hang_up));
    }

    private void dialogTypeDefault() {
        showAndHidden(mPassiveLlt, false);
        showAndHidden(mTypeIv, true);
        showAndHidden(mCloseTv, true);
        mNameTv.setText(VecConfig.newVecConfig().getTenantName());
        mContentTv.setText(mVideoStyleBean.getStyleSettings().getWaitingPrompt());
        mTypeIv.setImageResource(R.drawable.em_icon_call_accept);
        // 发起通话
        mTypeTv.setText(getResources().getString(R.string.vec_initiate_call));
    }

    private void dialogTypeNo() {
        showAndHidden(mTypeIv, false);
        showAndHidden(mTypeTv, false);
        showAndHiddenInvisible(mCloseTv, false);

        showAndHidden(mPassiveLlt, true);
        mNameTv.setText(getResources().getString(R.string.vec_initiate_call));
        mNameTv.setText(TextUtils.isEmpty(VecConfig.newVecConfig().getUserName()) ? getResources().getString(R.string.vec_initiate_call) : VecConfig.newVecConfig().getUserName());
        mContentTv.setText(TextUtils.isEmpty(mSmg) ? Utils.getString(getApplicationContext(), R.string.vec_agent_invitation) : mSmg);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentDialogType == DIALOG_TYPE_SEND
                || mCurrentDialogType == DIALOG_TYPE_WAIT || mCurrentDialogType == DIALOG_TYPE_RETRY) {
            return;
        }
        super.onBackPressed();
        clear();
    }

    private void finishPage() {
        postDelayed(() -> {
            clear();
            finish();
        }, 100);
    }


    private void clear() {
        WaitNetworkUtils.newWaitNetworkUtils().clear();
        stopTimerOut();
        Utils.clearDegreeTag(mDegreeBeanMap);
        AgoraMessage.newAgoraMessage().unRegisterIEndCallback(getClass().getSimpleName());
        if (mRatingBar != null){
            // mRatingBar.setOnRatingBarChangeListener(null);
            mRatingBar.setOnRatingChangeListener(null);
        }

        if (mEnquiryOptionsBean != null){
            mEnquiryOptionsBean.enquiryOptions.clear();
            mEnquiryOptionsBean = null;
        }
        removeHandlerAll();
        mToChatUserName = null;
        mIsCreate = false;
        mIsRun = false;
        mClickRequestPermission = false;
    }

    @Override
    public void onVecZuoXiToBreakOff() {
        finishPage();
    }

    @Override
    public void onInitWaitPage(int callType, String rtcSessionId, String tenantId) {
        EMLog.e(TAG, "访客主动邀请坐席 onInitWaitPage callType = "+callType);
        Log.e("ppppppppp","onInitWaitPage = "+callType);
        if (callType == 0){
            // 访客主动邀请坐席
            requestWait(rtcSessionId, tenantId);
        }
    }

    // 等待
    private void requestWait(String rtcSessionId, String tenantId) {
        WaitNetworkUtils.newWaitNetworkUtils().execute(rtcSessionId, tenantId, new WaitNetworkUtils.IWaitCallBack() {
            @Override
            public void onWaitData(boolean waitingFlag, String visitorWaitingNumber) {
                EMLog.e(TAG, "访客主动邀请坐席 onInitWaitPage onWaitData waitingFlag = "+waitingFlag);
                EMLog.e(TAG, "访客主动邀请坐席 onInitWaitPage onWaitData visitorWaitingNumber = "+visitorWaitingNumber);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()){
                            return;
                        }
                        if (mCurrentDialogType == DIALOG_TYPE_END){
                            return;
                        }
                        dialogTypeWaitUpdateUi(visitorWaitingNumber);
                    }
                });
            }

            @Override
            public void onWaitError(String errorMsg) {
                EMLog.e(TAG, "访客主动邀请坐席 onInitWaitPage onWaitError errorMsg = "+errorMsg);
            }
        });
    }

    private Map<Integer, ArrayList<FlowBean>> mDegreeBeanMap = new HashMap<>();
    private EnquiryOptionsBean mEnquiryOptionsBean;
    // 默认是否开启备注必填
    private boolean mIsEnquiryCommentEnable;
    // 解析满意度评价数据
    private void initRetry(Intent intent) {
        try {
            // 满意度评价
            String content = intent.getStringExtra("content");
            mEnquiryOptionsBean = EnquiryOptionsBean.get(content);

            JSONObject jsonObject = new JSONObject(content);
            Utils.getDegreeTag(mDegreeBeanMap, jsonObject, 5);
            // showAndHiddenInvisible(mCloseTv, false);
            showAndHiddenInvisible(mCloseTv, true);
            showAndHidden(mEvaluateFlt, true);
            mTitleTv.setText(Utils.getEnquiryInviteMsg(mEnquiryOptionsBean));
            mEvaluateTv.setText(Utils.getEnquirySolveMsg(mEnquiryOptionsBean));

            // 默认是否显示5星好评
            if (Utils.getEnquiryDefaultShow5Score(mEnquiryOptionsBean)){
                mCurrentRating = 5;
                //mRatingBar.setRating(5);
                mRatingBar.setStar(5);
                mFlowTagLayout.addContent(Utils.getDegreeTags(mDegreeBeanMap, 5));
            }else {
                mCurrentRating = 0;
                //mRatingBar.setRating(0);
                mRatingBar.setStar(0);
                showAndHidden(mFlowTagLayout, false);
            }

            mIsEnquiryCommentEnable = Utils.getEnquiryCommentEnable(mEnquiryOptionsBean);
            showAndHidden(mEtView, mIsEnquiryCommentEnable);

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    // 默认选择几颗星
    private int mCurrentRating;
    /*@Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (rating < 1.0f) {
            mCurrentRating = 0;
            ratingBar.setRating(0);
        }
        mCurrentRating = (int)rating;

        mShowTv.setText(Utils.getText(getApplicationContext(), (int)rating));
        if (mFlowTagLayout != null){
            showAndHidden(mFlowTagLayout, true);
            mFlowTagLayout.addContent(Utils.getDegreeTags(mDegreeBeanMap, (int)rating));
        }
    }*/

    @Override
    public void onRatingChange(float rating) {
        if (rating < 1.0f) {
            mCurrentRating = 0;
            //ratingBar.setRating(0);
            mRatingBar.setStar(0);
        }
        mCurrentRating = (int)rating;

        mShowTv.setText(Utils.getText(getApplicationContext(), (int)rating));
        if (mFlowTagLayout != null){
            showAndHidden(mFlowTagLayout, true);
            mFlowTagLayout.addContent(Utils.getDegreeTags(mDegreeBeanMap, (int)rating));
        }
    }

    // 提交满意度评价
    private void submitEvaluate() {
        List<DegreeBean> content = mFlowTagLayout.getContent();
        if (checkEvaluate(content)){
            showProgressTv();
            String comment = mEtView.getText().toString();


            SubmitEvaluationBean submitEvaluationBean = new SubmitEvaluationBean(
                    mEnquiryOptionsBean.rtcSessionId, mEnquiryOptionsBean.visitorUserId,
                    mCurrentRating, comment, content
            );

            Gson gson = new Gson();
            String jsonData = gson.toJson(submitEvaluationBean);
            AgoraMessage.asyncSubmitEvaluate(ChatClient.getInstance().tenantId(),
                    jsonData, new ValueCallBack<String>() {
                        @Override
                        public void onSuccess(String value) {
                            if (isFinishing()){
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hiddenProgressTv();
                                    if (value.contains("OK")){
                                        evaluateOk();
                                    }else {
                                        evaluateError();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(int error, String errorMsg) {
                            if (isFinishing()){
                                return;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hiddenProgressTv();
                                    evaluateError();
                                }
                            });
                        }
                    });
        }
    }

    private boolean checkEvaluate(List<DegreeBean> content) {
        if (mCurrentRating == 0){
            showToast(Utils.getString(getApplicationContext(), R.string.vec_select_star_evaluation));
            return false;
        }

        // 是否选择标签
        if (mCurrentRating == 1 || mCurrentRating == 2 || mCurrentRating == 3){
            if (Utils.getDegreeTagsEnable(mEnquiryOptionsBean, mCurrentRating)){
                // 判断是否选中标签
                if (content.size() == 0){
                    showToast(Utils.getString(getApplicationContext(), R.string.vec_select_tag));
                    return false;
                }
            }
        }

        // 默认是否开启备注必填
        if (mIsEnquiryCommentEnable){
            showAndHidden(mEtView, true);
            if (mCurrentRating == 1 || mCurrentRating == 2 || mCurrentRating == 3){
                if (Utils.getDegreeEnquiryCommentEnable(mEnquiryOptionsBean, mCurrentRating)){
                    if (TextUtils.isEmpty(mEtView.getText().toString().trim())){
                        showToast(Utils.getString(getApplicationContext(), R.string.vec_remarks));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void showToast(String content){
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    interface Callback {
        void run(String json) throws JSONException;
    }

    private void getTenantIdFunctionIcons(){
        // 动态获取功能按钮，在视频页面使用到
        AgoraMessage.asyncGetTenantIdFunctionIcons(ChatClient.getInstance().tenantId(), new ValueCallBack<List<FunctionIconItem>>() {
            @Override
            public void onSuccess(List<FunctionIconItem> value) {
                FlatFunctionUtils.get().setIconItems(value);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

}
