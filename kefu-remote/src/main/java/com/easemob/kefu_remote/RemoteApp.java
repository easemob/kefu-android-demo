package com.easemob.kefu_remote;

import android.content.Context;
import android.util.Log;

import com.easemob.kefu_remote.control.CtrlManager;
import com.easemob.kefu_remote.sdk.EMConferenceListener;
import com.easemob.kefu_remote.sdk.EMConferenceManager;
import com.easemob.kefu_remote.sdk.EMConferenceMember;
import com.easemob.kefu_remote.sdk.EMConferenceStream;
import com.easemob.kefu_remote.sdk.EMStreamStatistics;
import com.superrtc.mediamanager.EMediaManager;

import java.util.List;

public class RemoteApp {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private static RemoteApp instance;

    public RemoteApp() {

    }

    public static RemoteApp getInstance() {
        if (instance == null) {
            instance = new RemoteApp();
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 初始化 sdk
     */
    public void initSDK(Context context) {
        this.context = context;
        EMediaManager.initGlobal(context);
        EMConferenceManager.getInstance().addConferenceListener(new EMConferenceListener() {
            @Override public void onMemberJoined(EMConferenceMember member) {
                Log.d(TAG, "onMemberJoined:" + member);
            }

            @Override public void onMemberExited(EMConferenceMember member) {
                Log.d(TAG, "onMemberExited:" + member);
            }

            @Override public void onStreamAdded(EMConferenceStream stream) {
                Log.d(TAG, "onStreamAdded:" + stream);
            }

            @Override public void onStreamRemoved(EMConferenceStream stream) {
                Log.d(TAG, "onStreamRemoved:" + stream);
            }

            @Override public void onStreamUpdate(EMConferenceStream stream) {
                Log.d(TAG, "onStreamUpdate:" + stream);
            }

            @Override public void onPassiveLeave(int error, String message) {
                Log.d(TAG, "onPassiveLeave:" + message);
            }

            @Override public void onConferenceState(ConferenceState state, Object object) {
                Log.d(TAG, "onConferenceState:" + state);
            }

            @Override public void onStreamStatistics(EMStreamStatistics statistics) {
                Log.d(TAG, "onStreamStatistics:" + statistics);
            }

            @Override public void onStreamSetup(String streamId) {
                Log.d(TAG, "onStreamSetup:" + streamId);
            }

            @Override public void onSpeakers(List<String> speakers) {
                Log.d(TAG, "onSpeakers:" + speakers);
            }

            @Override public void onCtrlMessage(ConferenceState state, String arg1, String arg2, Object arg3) {
                if (state == ConferenceState.STATE_CUSTOM_MSG) {
                    Log.d(TAG, String.format("收到 【%s】 发来的 【%s】 自定义消息", arg2, arg1));
                } else if (state == ConferenceState.STATE_CTRL_MSG) {
                    Log.d(TAG, String.format("收到 【%s】 发来的 【%s】 控制消息", arg2, arg1));
                    CtrlManager.getInstance().parseCtrlMsg(arg1, arg2, arg3);
                } else if (state == ConferenceState.STATE_RESPONSE_MSG) {
                    Log.d(TAG, String.format("收到 【%s】 发来的 【%s】 响应消息", arg2, arg1));
                }
            }
        });
    }
}