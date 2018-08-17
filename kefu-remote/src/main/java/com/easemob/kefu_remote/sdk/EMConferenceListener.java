package com.easemob.kefu_remote.sdk;


import java.util.List;

/**
 * Created by lzan13 on 2017/8/16.
 * \~chinese
 * 多人音视频会议回调接口
 *
 * \~english
 * Multi person conference callback interface
 */
public interface EMConferenceListener {

    /**
     * \~chinese
     * 成员加入会议
     *
     * \~english
     * Member join conference
     */
    void onMemberJoined(EMConferenceMember member);

    /**
     * \~chinese
     * 成员离开会议
     *
     * \~english
     * Member exit conference
     */
    void onMemberExited(EMConferenceMember member);

    /**
     * \~chinese
     * 有新的成员推流
     *
     * \~english
     * New member publish stream
     */
    void onStreamAdded(EMConferenceStream stream);

    /**
     * \~chinese
     * 成员停止推流
     *
     * \~english
     * Member stop publish stream
     */
    void onStreamRemoved(EMConferenceStream stream);

    /**
     * \~chinese
     * 有成员更新自己的推流，比如打开摄像头，静音等操作
     *
     * \~english
     * Members to update their own flow, such as open the camera, mute and other operations
     */
    void onStreamUpdate(EMConferenceStream stream);

    /**
     * \~chinese
     * 被动离开会议
     *
     * \~english
     * Passively leave the conference
     */
    void onPassiveLeave(int error, String message);

    /**
     * \~chinese
     * 会议状态通知回调
     *
     * \~english
     * Conference status notification callback
     */
    void onConferenceState(ConferenceState state, Object object);

    /**
     * \~chinese
     * 统计信息回调
     *
     * \~english
     * Statistics
     */
    void onStreamStatistics(EMStreamStatistics statistics);

    /**
     * \~chinese
     * 推本地流 或 订阅成员流 成功回调
     * @param streamId 本地流 或 成员流ID
     *
     * \~english
     * stream publish or subscribe setup
     * @param streamId publish or subscribe stream id
     */
    void onStreamSetup(String streamId);

    /**
     * \~chinese
     * 当前说话者回调
     * @param speakers 当前说话的Stream id 集合
     *
     * \~english
     * Current speaking callback
     * @param speakers current speaking stream id list
     */
    void onSpeakers(List<String> speakers);

    /**
     * 自定义控制消息回调
     *
     * @param state 消息类型
     * @param arg1
     * @param arg2
     * @param arg3
     */
    void onCtrlMessage(ConferenceState state, String arg1, String arg2, Object arg3);

    /**
     * |~chinese
     * 会议通知状态
     *
     * \~english
     * conference notification state
     */
    enum ConferenceState {
        STATE_NORMAL,   // 正常状态
        STATE_STATISTICS,   // 统计信息
        STATE_DISCONNECTION,   // 连接断开
        STATE_RECONNECTION,    // 重新连接
        STATE_POOR_QUALITY,    // 通话质量差
        STATE_PUBLISH_SETUP,   // 推流完成
        STATE_SUBSCRIBE_SETUP, // 订阅完成
        STATE_TAKE_CAMERA_PICTURE,  // 捕获图片
        STATE_CUSTOM_MSG,   // 发送自定义消息
        STATE_CTRL_MSG,
        STATE_RESPONSE_MSG,
        STATE_UPDATE_PUB,
        STATE_UNPUB,
        STATE_AUDIO_TALKERS,    // 说话者
        STATE_P2P_PEER_EXIT,    // P2P 直连
        STATE_OPEN_CAMERA_FAIL, // 摄像头打开失败
        STATE_OPEN_MIC_FAIL     // 麦克风打开失败

    }

    enum ConferenceMode{
        NORMAL, // 普通模式
        LARGE   // 大会议模式，一般超过4人左右使用
    }
}
