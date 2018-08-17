package com.easemob.kefu_remote.sdk;

import android.view.View;

import com.superrtc.mediamanager.EMediaDefines;
import com.superrtc.mediamanager.EMediaPublishConfiguration;

/**
 * Created by lzan13 on 2017/8/16.
 * \~chinese
 * 本地推流配置信息类
 *
 * \~english
 * Local publish stream config
 */
public class EMStreamParam {

    protected String name;
    protected boolean videoOff;
    protected boolean audioOff;
    protected boolean useBackCamera;
    protected int videoWidth;
    protected int videoHeight;
    protected String extension;
    protected View shareView;
    protected EMConferenceStream.StreamType streamType;

    public EMStreamParam() {
        name = "AndroidNormal";
        videoOff = false;
        audioOff = false;
        useBackCamera = false;
        videoWidth = 240;
        videoHeight = 320;
        extension = "";
        shareView = null;
        streamType = EMConferenceStream.StreamType.NORMAL;
    }

    /**
     * \~chinese
     * 获取推流配置名称
     *
     * \~english
     * Get local publish stream name
     */
    public String getName() {
        return name;
    }

    /**
     * \~chinese
     * 设置本地推流名称
     *
     * \~english
     * Set local publish stream name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * \~chinese
     * 是否关闭视频
     *
     * \~english
     * Whether to turn off the video
     */
    public boolean isVideoOff() {
        return videoOff;
    }

    /**
     * \~chinese
     * 设置是否关闭视频
     *
     * \~english
     * Set whether to turn off the video
     */
    public void setVideoOff(boolean videoOff) {
        this.videoOff = videoOff;
    }

    /**
     * \~chinese
     * 是否静音
     *
     * \~english
     * Whether mute
     */
    public boolean isAudioOff() {
        return audioOff;
    }

    /**
     * \~chinese
     * 设置静音
     *
     * \~english
     * Set whether mute
     */
    public void setAudioOff(boolean audioOff) {
        this.audioOff = audioOff;
    }

    /**
     * \~chinese
     * 使用后置摄像头 默认为 false
     *
     * @param useBackCamera 使用后置摄像头
     *
     * \~english
     * Use back camera, default false
     * @param useBackCamera Use back camera
     */
    public void setUseBackCamera(boolean useBackCamera) {
        this.useBackCamera = useBackCamera;
    }

    public boolean isUseBackCamera() {
        return useBackCamera;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    /**
     * \~chinese
     * 获取推流扩展
     *
     * \~english
     * Get publish stream extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * \~chinese
     * 设置推流扩展信息
     *
     * \~english
     * Set local publish stream extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }


    public View getShareView() {
        return shareView;
    }

    public void setShareView(View shareView) {
        this.shareView = shareView;
    }

    public EMConferenceStream.StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(EMConferenceStream.StreamType streamType) {
        this.streamType = streamType;
    }
}

