package com.easemob.kefu_remote.sdk;

import android.graphics.Bitmap;
import android.util.Log;

import com.easemob.kefu_remote.RemoteApp;
import com.superrtc.mediamanager.EMediaDefines;
import com.superrtc.mediamanager.EMediaEntities;
import com.superrtc.mediamanager.EMediaManager;
import com.superrtc.mediamanager.EMediaPublishConfiguration;
import com.superrtc.mediamanager.EMediaSession;
import com.superrtc.mediamanager.EMediaStream;
import com.superrtc.mediamanager.EMediaTalker;
import com.superrtc.sdk.RtcConnection;
import com.superrtc.sdk.VideoView;
import com.superrtc.sdk.VideoViewRenderer;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lzan13 on 2017/8/16.
 * \~chinese
 * 多人音视频会议管理类，封装多人音视频会议操作方法，创建，加入，邀请等
 * <p>
 * \~english
 * Multi person conference manager, Encapsulation of multi-person audio and video conference operation method,
 * create, join, invite and so on
 */
public class EMConferenceManager {
    private final String TAG = this.getClass().getSimpleName();

    private EMediaManager mediaManager = null;
    private EMediaSession mediaSession = null;

    private static EMConferenceManager instance;
    private ExecutorService executorService;

    private List<EMConferenceListener> conferenceListeners = Collections.synchronizedList(new ArrayList<EMConferenceListener>());
    private Map<String, EMConferenceMember> memberMap = new ConcurrentHashMap<>();
    private Map<String, EMConferenceStream> availableStreamMap = new ConcurrentHashMap<>();
    private Map<String, EMConferenceStream> subscribedStreamMap = new ConcurrentHashMap<>();

    private EMConferenceListener.ConferenceMode conferenceMode = EMConferenceListener.ConferenceMode.LARGE;

    /**
     * preventing to instantiate this EMConferenceManager
     */
    protected EMConferenceManager() {
        if (!EMediaManager.isInit()) {

            EMediaManager.initGlobal(new RemoteApp().getContext());
        }
        mediaManager = EMediaManager.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        setSubscribeAudioMixEnable();
    }

    public static EMConferenceManager getInstance() {
        if (instance == null) {
            instance = new EMConferenceManager();
        }
        return instance;
    }

    /**
     * \~chinese
     * 添加会议监听
     *
     * \~english
     * Add conference listener
     */
    public void addConferenceListener(EMConferenceListener listener) {
        if (listener != null && !conferenceListeners.contains(listener)) {
            conferenceListeners.add(listener);
        }
    }

    /**
     * \~chinese
     * 移除会议监听
     *
     * \~english
     * Remove conference listener
     */
    public void removeConferenceListener(EMConferenceListener listener) {
        if (listener != null) {
            conferenceListeners.remove(listener);
        }
    }

    /**
     * 加入会议
     */
    public void joinConference(final String ticket, final String username,
            final EMStreamParam param, final EMCallbacks callback) {
        Log.d(TAG, "joinConference");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Join conference");
                    mediaSession = mediaManager.newSessionWithTicket(ticket, "{\"identity\":\"visitor\"}", sessionDelegate);

                    mediaManager.setSession(mediaSession, username);
                    param.extension = "{\"identity\":\"visitor\"}";
                    mediaManager.join(mediaSession, configWrap(param), new EMediaEntities.EMediaIdBlockType() {
                        @Override
                        public void onDone(Object uid, EMediaEntities.EMediaError error) {
                            if (error != null) {
                                Log.d(TAG, "Join conference failed code=" + error.code + ", desc=" + error.errorDescription);
                                if (callback != null) {
                                    callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                                }
                            } else {
                                Log.d(TAG, "Join conference success!");
                                if (callback != null) {
                                    callback.onDone(uid);
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * \~chinese
     * 退出会议
     *
     * \~english
     * Exit conference
     */
    public void exitConference(final EMCallbacks callback) {
        Log.d(TAG, "Exit conference - async");
        mediaManager.exit(mediaSession, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Exit conference failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Exit conference success");
                    if (callback != null) {
                        callback.onDone(null);
                    }
                }
                memberMap.clear();
                subscribedStreamMap.clear();
                availableStreamMap.clear();
            }
        });
    }

    /**
     * \~chinese
     * 本地推流
     *
     * @param param 推送本地流时配置信息
     * @param callback 结果回调
     *
     * \~english
     * Publish local stream
     * @param param publish local stream config
     * @param callback result callback
     */
    public void publish(EMStreamParam param, final EMCallbacks callback) {
        Log.d(TAG, "Publish local stream");
        param.extension = "{\"identity\":\"visitor\"}";
        mediaManager.publish(mediaSession, configWrap(param), new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Publish failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Publish success Stream id - " + uid);
                    if (callback != null) {
                        callback.onDone(uid);
                    }
                }
            }
        });
    }

    /**
     * \~chinese
     * 取消本地推流
     *
     * @param pubStreamId 本地数据流 id
     *
     * \~english
     * UNPublish local stream
     * @param pubStreamId local stream id
     */
    public void unpublish(String pubStreamId, final EMCallbacks callback) {
        Log.d(TAG, "UNPublish local stream - async");
        mediaManager.unpublish(mediaSession, pubStreamId, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Unpublish failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Unpublish success Stream id - " + uid);
                    if (callback != null) {
                        callback.onDone(uid);
                    }
                }
            }
        });
    }

    /**
     * \~chinese
     * 订阅成员推送流数据
     *
     * @param stream 当前操作的流
     * @param surfaceView 用来显示订阅的流画面的控件
     * @param callback 结果回调
     *
     * \~english
     * Subscribe member publish stream
     * @param stream current stream
     * @param surfaceView Displays the remote image controls
     * @param callback result callback
     */
    public void subscribe(final EMConferenceStream stream, VideoView surfaceView,
            final EMCallbacks callback) {
        Log.d(TAG, "Subscribe stream - async " + stream.toString());
        VideoViewRenderer renderer = surfaceView != null ? surfaceView.getRenderer() : null;
        mediaManager.subscribe(mediaSession, stream.getStreamId(), renderer, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Subscribe failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Subscribe success Stream id - " + uid);
                    subscribedStreamMap.put(stream.getStreamId(), stream);
                    availableStreamMap.remove(stream.getStreamId());
                    if (callback != null) {
                        callback.onDone(uid);
                    }
                }
            }
        });
    }

    /**
     * \~chinese
     * 更新订阅成员推送流数据
     *
     * @param stream 当前操作的流
     * @param surfaceView 用来显示订阅的流画面的控件
     * @param callback 结果回调
     *
     * \~english
     * Update subscribe member publish stream
     * @param stream current stream
     * @param surfaceView Displays the remote image controls
     * @param callback result callback
     */
    public void updateSubscribe(final EMConferenceStream stream, VideoView surfaceView,
            final EMCallbacks callback) {
        Log.d(TAG, "Subscribe stream - async " + stream.toString());
        VideoViewRenderer renderer = surfaceView != null ? surfaceView.getRenderer() : null;
        mediaManager.updateSubscribe(mediaSession, stream.getStreamId(), renderer, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Update subscribe failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Update subscribe success Stream id - " + uid);
                    subscribedStreamMap.put(stream.getStreamId(), stream);
                    availableStreamMap.remove(stream.getStreamId());
                    if (callback != null) {
                        callback.onDone(uid);
                    }
                }
            }
        });
    }

    /**
     * \~chinese
     * 取消订阅成员推送流数据
     *
     * @param stream 当前流
     * @param callback 结果回调接口
     *
     * \~english
     * Unsubscribe member publish stream
     * @param stream current stream
     * @param callback result callback
     */
    public void unsubscribe(final EMConferenceStream stream, final EMCallbacks callback) {
        Log.d(TAG, "UNSubscribe stream - async " + stream.toString());
        mediaManager.unsubscribe(mediaSession, stream.getStreamId(), new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                if (error != null) {
                    Log.d(TAG, "Unsubscribe failed code=" + error.code + ", desc=" + error.errorDescription);
                    if (callback != null) {
                        callback.onError(errorMap(error.code.errorcode), error.errorDescription);
                    }
                } else {
                    Log.d(TAG, "Unsubscribe success Stream id - " + uid);
                    availableStreamMap.put(stream.getStreamId(), stream);
                    subscribedStreamMap.remove(stream.getStreamId());
                    if (callback != null) {
                        callback.onDone(uid);
                    }
                }
            }
        });
    }

    /**
     * \~chinese
     * 外部输入视频数据方法，此方法主要是为分享桌面回调使用
     *
     * @param bitmap 捕获的 bitmap 数据
     *
     * \~english
     * Input external video data
     * @param bitmap Share view capture bitmap
     */
    public void inputExternalVideoData(Bitmap bitmap) {
        mediaManager.inputExternalVideoData(bitmap);
    }

    /**
     * \~chinese
     * 外部输入数据方法
     *
     * @param data 视频数据流，需要是 YUV 格式数据
     * @param width 视频数据帧宽
     * @param height 视频数据帧高
     * @param rotation 旋转角度
     *
     * \~english
     * Input external view data
     * @param data Video data YUV format
     * @param width Video frame width
     * @param height Video frame height
     * @param rotation Video frame rotation
     */
    public void inputExternalVideoData(byte[] data, int width, int height, int rotation) {
        mediaManager.inputExternalVideoData(data, width, height, rotation);
    }

    public void setVideoMaxKbps(int videoMaxKbps) {
        mediaManager.setVideoMaxKbps(videoMaxKbps);
    }

    public void setVideoMinKbps(int videoMinKbps) {
        mediaManager.setVideoMinKbps(videoMinKbps);
    }

    public void setAudioMaxKbps(int audioMaxKbps) {
        mediaManager.setAudioMaxKbps(audioMaxKbps);
    }

    /**
     * 设置 rtc 录制文件保存地址
     */
    public void setDocDirectory(String path) {
        mediaManager.setDocDirectory(path);
    }

    public boolean startRecordPlayout() {
        return mediaManager.startRecordPlayout();
    }

    public String stopRecordPlayout() {
        return mediaManager.stopRecordPlayout();
    }

    /**
     * 设置 rtc 服务自定义 url 和 host
     */
    public void setCustomUrl(String url, String host) {
        mediaManager.setSpecificServerUrl(url, host);
    }

    /**
     * \~chinese
     * 开启正在说话监听器
     *
     * @param interval {@link EMConferenceListener#onSpeakers(List)} 回调间隔
     *
     * \~english
     * Start speaking monitor
     * @param interval {@link EMConferenceListener#onSpeakers(List)} interval
     */
    public void startMonitorSpeaker(int interval) {
        mediaManager.setAudioTalkerInterval(interval, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {
                Log.d(TAG, "error: " + error.code + " desc:" + error.errorDescription);
            }
        });
    }

    /**
     * \~chinese
     * 停止正在说话监听器
     *
     * \~english
     * Stop speaker monitor
     */
    public void stopMonitorSpeaker() {
        mediaManager.stopAudioTalker();
    }


    /**
     * \~chinese
     * 设置会议模式
     *
     * \~english
     * config conference mode
     */
    public void setConferenceMode(EMConferenceListener.ConferenceMode mode) {
        conferenceMode = mode;
        setSubscribeAudioMixEnable();
    }

    /**
     * 通知 rtc 底层是否订阅 mix 流
     */
    private void setSubscribeAudioMixEnable() {
        if (conferenceMode == EMConferenceListener.ConferenceMode.LARGE) {
            mediaManager.setSubscribeAudioMixEnabled(false);
        } else {
            mediaManager.setSubscribeAudioMixEnabled(true);
        }
    }

    public void cameraFocuse(float x, float y, int width, int height) {
        mediaManager.manualFocus(x, y, width, height);
    }

    public void cameraZoom(boolean zoom, int zoomScale) {
        mediaManager.manualZoom(zoom, zoomScale);
    }

    /**
     * \~chinese
     * 设置显示自己本地预览画面控件
     *
     * @param localView 显示本地图像的控件
     *
     * \~english
     * Set local surface view
     * @param localView Displays the local image controls
     */
    public void setLocalSurfaceView(VideoView localView) {
        VideoViewRenderer localRender = null;
        if (localView != null) {
            localRender = localView.getRenderer();
        }
        mediaManager.setLocalPreviewView(localRender);
    }

    /**
     * \~chinese
     * 更新显示本地画面控件
     *
     * @param localView 显示本地图像的控件
     *
     * \~english
     * Update local surface view
     * @param localView Displays the local image controls
     */
    public void updateLocalSurfaceView(VideoView localView) {
        mediaManager.setVideoViews(null, localView.getRenderer(), null, true);
    }

    /**
     * \~chinese
     * 更新显示远端画面控件
     *
     * @param streamId 当前控件显示的流 id
     * @param remoteView 显示远端图像控件
     *
     * \~english
     * Update remote surface view
     * @param streamId current stream id
     * @param remoteView Displays the remote image controls
     */
    public void updateRemoteSurfaceView(String streamId, VideoView remoteView) {
        mediaManager.setVideoViews(streamId, null, remoteView.getRenderer(), false);
    }

    /**
     * 通过成员发送自定义消息
     */
    public void sendCtrlMsgByMemberId(String memberId, int code, Object obj, String message) {
        mediaManager.sendCtrlMsgByMemberId(mediaSession, memberId, code, obj, message, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {

            }
        });
    }

    public void sendCtrlMsgByStreamId(String streamId, int code, Object obj, String message) {
        mediaManager.sendCtrlMsgByStreamId(mediaSession, streamId, code, obj, message, new EMediaEntities.EMediaIdBlockType() {
            @Override
            public void onDone(Object uid, EMediaEntities.EMediaError error) {

            }
        });
    }

    /**
     * \~chinese
     * 获取当前摄像头 id， 0 表示后置摄像头，1 表示前置摄像头
     *
     * \~english
     * get current camera id, 0 back, 1 front
     */
    public int getCameraId() {
        return mediaManager.getCameraFacing();
    }

    /**
     * \~chinese
     * 切换摄像头
     *
     * \~english
     * Switch camera
     */
    public void switchCamera() {
        mediaManager.switchCamera();
    }

    /**
     * \~chinese
     * 关闭视频传输
     *
     * \~english
     * Close video transfer
     */
    public void closeVideoTransfer() {
        mediaManager.setVideoEnabled(false);
    }

    /**
     * \~chinese
     * 打开视频传输
     *
     * \~english
     * Open video transfer
     */
    public void openVideoTransfer() {
        mediaManager.setVideoEnabled(true);
    }

    /**
     * \~chinese
     * 关闭语音传输
     *
     * \~english
     * Close voice transfer
     */
    public void closeVoiceTransfer() {
        mediaManager.setMuteEnabled(true);
    }

    /**
     * \~chinese
     * 打开语音传输
     *
     * \~english
     * Open voice transfer
     */
    public void openVoiceTransfer() {
        mediaManager.setMuteEnabled(false);
    }

    /**
     * \~chinese
     * 启用统计
     *
     * @param enable 是否启用统计
     *
     * \~english
     * enable statistics
     * @params enable enable statistics
     */
    public void enableStatistics(boolean enable) {
        mediaManager.enableStatistics(mediaSession, enable);
    }

    //public void setAECConfig(String key, int value) {
    //    mediaManager.setAECConfig(key, value);
    //}
    //
    //public void setAECConfig(String key, String value) {
    //    mediaManager.setAECConfig(key, value);
    //}

    /**
     * \~chinese
     * 获取当前会议成员
     *
     * \~english
     * Get conference member list
     */
    public Map<String, EMConferenceMember> getMemberMap() {
        return memberMap;
    }

    /**
     * \~chinese
     * 获取当前会议可订阅 Stream
     *
     * \~english
     * Get subscribable stream map
     */
    public Map<String, EMConferenceStream> getAvailableStreamMap() {
        return availableStreamMap;
    }

    /**
     * \~chinese
     * 获取当前会议已订阅 Stream
     *
     * \~english
     * get subscribed stream list
     */
    public Map<String, EMConferenceStream> getSubscribedStreamMap() {
        return subscribedStreamMap;
    }

    /**
     * 多人会议监听器
     */
    EMediaSession.EMediaSessionDelegate sessionDelegate = new EMediaSession.EMediaSessionDelegate() {
        @Override
        public void joinMember(EMediaSession mediaSession,
                EMediaEntities.EMediaMember mediaMember) {
            Log.d(TAG, "onMemberJoined() memberName: " + mediaMember.memberName + ", extension: " + mediaMember.extension);

            EMConferenceMember member = new EMConferenceMember(mediaMember);
            if (!memberMap.containsKey(mediaMember.memberName)) {
                memberMap.put(mediaMember.memberName, member);
            }
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onMemberJoined(member);
                }
            }
        }

        @Override
        public void exitMember(EMediaSession mediaSession,
                EMediaEntities.EMediaMember mediaMember) {
            Log.d(TAG, "onMemberExited() memberName: " + mediaMember.memberName + ", extension: " + mediaMember.extension);
            EMConferenceMember member = null;
            if (memberMap.containsKey(mediaMember.memberName)) {
                member = memberMap.get(mediaMember.memberName);
                memberMap.remove(mediaMember.memberName);
            } else {
                member = new EMConferenceMember(mediaMember);
            }
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onMemberExited(member);
                }
            }
        }

        @Override
        public void addStream(EMediaSession mediaSession, EMediaStream mediaStream) {
            Log.d(TAG, "onStreamAdded() memberName: " + mediaStream.memberName + ", extension: " + mediaStream.extension + ", streamId: " + mediaStream.streamId + ", streamName: " + mediaStream.streamName + ", streamType: " + mediaStream.streamType + ", audioOff: " + mediaStream.audioOff + ", videoOff: " + mediaStream.videoOff);
            EMConferenceStream stream = new EMConferenceStream();
            stream.init(mediaStream);
            availableStreamMap.put(stream.getStreamId(), stream);
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onStreamAdded(stream);
                }
            }
        }

        @Override
        public void removeStream(EMediaSession mediaSession, EMediaStream mediaStream) {
            Log.d(TAG, "onStreamRemoved() memberName: " + mediaStream.memberName + ", extension: " + mediaStream.extension + ", streamId: " + mediaStream.streamId + ", streamName: " + mediaStream.streamName + ", streamType: " + mediaStream.streamType + ", audioOff: " + mediaStream.audioOff + ", videoOff: " + mediaStream.videoOff);
            EMConferenceStream stream = null;
            if (availableStreamMap.containsKey(mediaStream.streamId)) {
                stream = availableStreamMap.get(mediaStream.streamId);
                availableStreamMap.remove(mediaStream.streamId);
            } else if (subscribedStreamMap.containsKey(mediaStream.streamId)) {
                stream = subscribedStreamMap.get(mediaStream.streamId);
                subscribedStreamMap.remove(mediaStream.streamId);
            } else {
                stream = new EMConferenceStream();
            }
            stream.init(mediaStream);
            mediaManager.unsubscribe(mediaSession, mediaStream.streamId, null);
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onStreamRemoved(stream);
                }
            }
        }

        @Override
        public void updateStream(EMediaSession mediaSession, EMediaStream mediaStream) {
            Log.d(TAG, "onStreamUpdate() memberName: " + mediaStream.memberName + ", extension: " + mediaStream.extension + ", streamId: " + mediaStream.streamId + ", streamName: " + mediaStream.streamName + ", streamType: " + mediaStream.streamType + ", audioOff: " + mediaStream.audioOff + ", videoOff: " + mediaStream.videoOff);
            EMConferenceStream stream = null;
            if (availableStreamMap.containsKey(mediaStream.streamId)) {
                stream = availableStreamMap.get(mediaStream.streamId);
            } else if (subscribedStreamMap.containsKey(mediaStream.streamId)) {
                stream = subscribedStreamMap.get(mediaStream.streamId);
            } else {
                stream = new EMConferenceStream();
            }
            stream.init(mediaStream);
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onStreamUpdate(stream);
                }
            }
        }

        @Override
        public void passiveCloseReason(EMediaSession mediaSession, int code, String reason) {
            Log.d(TAG, "onPassiveLeave() code: " + code + ", reason: " + reason);
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    listener.onPassiveLeave(code, reason);
                }
            }
        }

        @Override
        public void notice(EMediaSession mediaSession,
                EMediaDefines.EMediaNoticeCode mediaNoticeCode, String arg1, String arg2,
                Object arg3) {
            Log.d(TAG, "Notice code: " + mediaNoticeCode + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3);
            synchronized (conferenceListeners) {
                for (EMConferenceListener listener : conferenceListeners.subList(0, conferenceListeners
                        .size())) {
                    EMConferenceListener.ConferenceState state = stateMap(mediaNoticeCode.noticeCode);
                    if (state == EMConferenceListener.ConferenceState.STATE_PUBLISH_SETUP) {
                        listener.onStreamSetup(arg1);
                    } else if (state == EMConferenceListener.ConferenceState.STATE_SUBSCRIBE_SETUP) {
                        listener.onStreamSetup(arg1);
                    } else if (state == EMConferenceListener.ConferenceState.STATE_STATISTICS) {
                        listener.onStreamStatistics(new EMStreamStatistics(arg2, (RtcConnection.RtcStatistics) arg3));
                    } else if (state == EMConferenceListener.ConferenceState.STATE_P2P_PEER_EXIT || state == EMConferenceListener.ConferenceState.STATE_TAKE_CAMERA_PICTURE) {

                    } else if (state == EMConferenceListener.ConferenceState.STATE_CUSTOM_MSG || state == EMConferenceListener.ConferenceState.STATE_CTRL_MSG || state == EMConferenceListener.ConferenceState.STATE_RESPONSE_MSG) {
                        listener.onCtrlMessage(state, arg1, arg2, arg3);
                    } else if (state == EMConferenceListener.ConferenceState.STATE_AUDIO_TALKERS) {
                        Map<String, EMediaTalker> talkers = (Map<String, EMediaTalker>) arg3;
                        List<String> speakers = new ArrayList<>(talkers.keySet());
                        listener.onSpeakers(speakers);
                    } else {
                        listener.onConferenceState(state, arg3);
                    }
                }
            }
        }
    };

    /**
     * 包装 publish 配置信息
     *
     * @param param 用户提供的配置参数
     * @return 包装成 RTC 底层需要的参数
     */
    private EMediaPublishConfiguration configWrap(EMStreamParam param) {
        EMediaPublishConfiguration config = null;
        if (param == null) {
            return config;
        }
        if (param.streamType == EMConferenceStream.StreamType.NORMAL) {
            config = EMediaPublishConfiguration.initNormalConfig();
        } else if (param.streamType == EMConferenceStream.StreamType.AUDIOMIX) {
            config = EMediaPublishConfiguration.initAudioMixConfig();
        } else {
            config = EMediaPublishConfiguration.initDesktopConfig();
        }
        config.setExtension(param.extension);
        config.setMute(param.audioOff);
        config.setVideoOff(param.videoOff);
        config.setVwidth(param.videoWidth);
        config.setVheight(param.videoHeight);
        config.setPubView(param.shareView);
        config.setUseBackCamera(param.useBackCamera);
        return config;
    }

    /**
     * 错误码映射
     *
     * @param code rtc 错误码
     * @return im 错误码
     */
    private int errorMap(int code) {
        //        switch (code) {
        //        case 0:
        //            error = EMError.EM_NO_ERROR;
        //            break;
        //        case -1:
        //            error = EMError.GENERAL_ERROR;
        //            break;
        //        case -102:
        //            error = EMError.CALL_INVALID_PARAMS;
        //            break;
        //        case -106:
        //            error = EMError.CALL_CONFERENCE_CANCEL;
        //            break;
        //        case -108:
        //            error = EMError.NETWORK_ERROR;
        //            break;
        //        case -109:
        //            error = EMError.CALL_CONNECTION_ERROR;
        //            break;
        //        case -112:
        //            error = EMError.CALL_CONNECTION_TIMEOUT;
        //            break;
        //        case -113:
        //            error = EMError.CALL_JOIN_TIMEOUT;
        //            break;
        //        case -122:
        //            error = EMError.CALL_ALREADY_JOIN;
        //            break;
        //        case -123:
        //            error = EMError.CALL_ALREADY_PUB;
        //            break;
        //        case -124:
        //            error = EMError.CALL_ALREADY_SUB;
        //            break;
        //        case -142:
        //            error = EMError.CALL_NO_SESSION;
        //            break;
        //        case -143:
        //            error = EMError.CALL_NO_PUBLISH;
        //            break;
        //        case -144:
        //            error = EMError.CALL_NO_SUBSCRIBE;
        //            break;
        //        case -145:
        //            error = EMError.CALL_NO_STREAM;
        //            break;
        //        case -404:
        //            error = EMError.CALL_CONNECTION_ERROR;
        //            break;
        //        case -410:
        //            error = EMError.CALL_OTHER_DEVICE;
        //            break;
        //        case -411:
        //            error = EMError.CALL_CONFERENCE_DISMISS;
        //            break;
        //        case -500:
        //            error = EMError.CALL_TICKET_INVALID;
        //            break;
        //        case -502:
        //            error = EMError.CALL_TICKET_EXPIRED;
        //            break;
        //        case -504:
        //            error = EMError.CALL_SESSION_EXPIRED;
        //            break;
        //        case -506:
        //            error = EMError.CALL_CONFERENCE_NO_EXIST;
        //            break;
        //        default:
        //            error = EMError.GENERAL_ERROR;
        //            break;
        //        }
        return code;
    }

    /**
     * 状态映射
     *
     * @param state rtc 返回状态
     * @return im 状态
     */
    private EMConferenceListener.ConferenceState stateMap(int state) {
        EMConferenceListener.ConferenceState conferenceState;
        switch (state) {
        case 0:
            conferenceState = EMConferenceListener.ConferenceState.STATE_NORMAL;
            break;
        case 100:
            conferenceState = EMConferenceListener.ConferenceState.STATE_STATISTICS;
            break;
        case 120:
            conferenceState = EMConferenceListener.ConferenceState.STATE_DISCONNECTION;
            break;
        case 121:
            conferenceState = EMConferenceListener.ConferenceState.STATE_RECONNECTION;
            break;
        case 122:
            conferenceState = EMConferenceListener.ConferenceState.STATE_POOR_QUALITY;
            break;
        case 123:
            conferenceState = EMConferenceListener.ConferenceState.STATE_PUBLISH_SETUP;
            break;
        case 124:
            conferenceState = EMConferenceListener.ConferenceState.STATE_SUBSCRIBE_SETUP;
            break;
        case 125:
            conferenceState = EMConferenceListener.ConferenceState.STATE_TAKE_CAMERA_PICTURE;
            break;
        case 126:
            conferenceState = EMConferenceListener.ConferenceState.STATE_CUSTOM_MSG;
            break;
        case 127:
            conferenceState = EMConferenceListener.ConferenceState.STATE_CTRL_MSG;
            break;
        case 128:
            conferenceState = EMConferenceListener.ConferenceState.STATE_RESPONSE_MSG;
            break;
        case 129:
            conferenceState = EMConferenceListener.ConferenceState.STATE_UPDATE_PUB;
            break;
        case 130:
            conferenceState = EMConferenceListener.ConferenceState.STATE_UNPUB;
            break;
        case 131:
            conferenceState = EMConferenceListener.ConferenceState.STATE_AUDIO_TALKERS;
            break;
        case 181:
            conferenceState = EMConferenceListener.ConferenceState.STATE_P2P_PEER_EXIT;
            break;
        case 201:
            conferenceState = EMConferenceListener.ConferenceState.STATE_OPEN_CAMERA_FAIL;
            break;
        case 202:
            conferenceState = EMConferenceListener.ConferenceState.STATE_OPEN_MIC_FAIL;
            break;
        default:
            conferenceState = EMConferenceListener.ConferenceState.STATE_NORMAL;
            break;
        }
        return conferenceState;
    }
}
