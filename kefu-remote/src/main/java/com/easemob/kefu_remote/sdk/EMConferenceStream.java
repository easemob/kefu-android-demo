package com.easemob.kefu_remote.sdk;

import com.superrtc.mediamanager.EMediaStream;

/**
 * Created by lzan13 on 2017/8/16.
 * \~chinease
 * 多人会议中其他成员推流数据，这里边保存流一些基本信息
 *
 * \~english
 * Multi-person conference other members of the flow of data, here to save some basic information flow
 */
public class EMConferenceStream {
    private String streamId;
    private String streamName;
    private String memberName;
    private String username;
    private String extension;
    private boolean videoOff;
    private boolean audioOff;
    private StreamType streamType;

    public void init(EMediaStream stream) {
        setStreamId(stream.streamId);
        setStreamName(stream.streamName);
        setMemberName(stream.memberName);
        setUsername(memberName);
        setExtension(stream.extension);
        setVideoOff(stream.videoOff);
        setAudioOff(stream.audioOff);
        setStreamType(stream.streamType.val);
    }

    /**
     * \~chinese
     * 获取流数据 ID
     *
     * \~english
     * Get stream id
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * \~chinese
     * 设置流 ID
     *
     * \~english
     * Set stream id
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    /**
     * \~chinese
     * 获取当前流名称
     *
     * \~english
     * Get stream name
     */
    public String getStreamName() {
        return streamName;
    }

    /**
     * \~chinese
     * 设置流名称
     *
     * \~english
     * Set stream name
     */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    /**
     * \~chinese
     * 获取当前流对应成员名称
     *
     * \~english
     * Get current steam member name
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * \~chinese
     * 设置当前流对应的成员名
     *
     * \~english
     * Set current stream member name
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * \~chinese
     * 获取当前流对应的用户名
     *
     * \~english
     * Get current stream username
     */
    public String getUsername() {
        return username;
    }

    /**
     * \~chinese
     * 设置当前流对应的用户名
     *
     * \~english
     * Set current stream username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * \~chinese
     * 获取当前流包含的扩展信息
     *
     * \~english
     * Get current stream extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * \~chinese
     * 设置当前流扩展信息
     *
     * \~english
     * Set current stream extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * \~chinese
     * 判断当前流是否关闭视频
     *
     * \~english
     * Determines whether the current stream is off the video
     */
    public boolean isVideoOff() {
        return videoOff;
    }

    /**
     * \~chinese
     * 设置当前流是否关闭视频
     *
     * \~english
     * Set whether the current stream is off the video
     */
    public void setVideoOff(boolean videoOff) {
        this.videoOff = videoOff;
    }

    /**
     * \~chinese
     * 判断当前流是否关闭音频
     *
     * \~english
     * Determines whether the current stream is off the audio
     */
    public boolean isAudioOff() {
        return audioOff;
    }

    /**
     * \~chinese
     * 设置当前流是否关闭音频
     *
     * \~english
     * Set whether the current stream is off the audio
     */
    public void setAudioOff(boolean audioOff) {
        this.audioOff = audioOff;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        if (streamType == 0) {
            this.streamType = StreamType.NORMAL;
        } else if (streamType == 1) {
            this.streamType = StreamType.DESKTOP;
        } else if (streamType == 2) {
            this.streamType = StreamType.AUDIOMIX;
        }
    }

    public enum StreamType {
        NORMAL,
        DESKTOP,
        AUDIOMIX;
    }
}
