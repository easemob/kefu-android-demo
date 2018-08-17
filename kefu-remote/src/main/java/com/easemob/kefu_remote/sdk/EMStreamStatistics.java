package com.easemob.kefu_remote.sdk;

import com.superrtc.sdk.RtcConnection;

/**
 * Created by lzan13 on 2018/5/4.
 * 音视频通话统计信息实体类
 */
public class EMStreamStatistics {

    private String streamId;
    /**
     * 发送信息统计
     */
    // 发送视频捕获的宽高
    private int localCaptureWidth = 0;
    private int localCaptureHeight = 0;
    // 发送视频期望帧率
    private int localCaptureFps = 0;
    // 发送视频编码宽高
    private int localEncodedWidth = 0;
    private int localEncodedHeight = 0;
    // 发送视频编码帧率
    private int localEncodedFps = 0;
    // 发送视频比特率，单位为 kbps
    private int localVideoActualBps = 0;
    // 发送视频目标比特率，单位为 kbps
    private int localVideoTargetBps = 0;
    // 发送视频丢包数
    private int localVideoPacketsLost = 0;
    // 发送视频丢包率 每一百个包中丢包个数
    private int localVideoPacketsLostrate = 0;
    // 发送视频延时 单位是 ms
    private int localVideoRtt = 0;
    // 发送音频丢包数
    private int localAudioPacketsLost = 0;
    // 发送音频丢包率 每一百个包中丢包个数
    private int localAudioPacketsLostrate = 0;
    // 发送音频比特率，单位为 kbps
    private int localAudioBps = 0;
    // 发送音频延迟 单位是 ms
    private int localAudioRtt = 0;

    /**
     * 接收统计信息
     */
    // 接收视频宽高
    private int remoteWidth = 0;
    private int remoteHeight = 0;
    // 接收帧率
    private int remoteFps = 0;
    // 接受视频丢包数
    private int remoteVideoPacketsLost = 0;
    // 接收视频丢包率 每一百个包中丢包个数
    private int remoteVideoPacketsLostrate = 0;
    // 接收视频比特率，单位为kbps
    private int remoteVideoBps = 0;
    // 接受音频丢包数
    private int remoteAudioPacketsLost = 0;
    // 接受音频丢包率 每一百个包中丢包个数
    private int remoteAudioPacketsLostrate = 0;
    // 接受音频比特率，单位为 kbps
    private int remoteAudioBps = 0;

    public EMStreamStatistics(String streamId, RtcConnection.RtcStatistics statistics) {
        this.streamId = streamId;
        localCaptureWidth = statistics.localCaptureWidth;
        localCaptureHeight = statistics.localCaptureHeight;
        localCaptureFps = statistics.localCaptureFps;
        localEncodedWidth = statistics.localEncodedWidth;
        localEncodedHeight = statistics.localEncodedHeight;
        localEncodedFps = statistics.localEncodedFps;
        localVideoActualBps = statistics.localVideoActualBps;
        localVideoTargetBps = statistics.localVideoTargetBps;
        localVideoPacketsLost = statistics.localVideoPacketsLost;
        localVideoPacketsLostrate = statistics.localVideoPacketsLostrate;
        localVideoRtt = statistics.localVideoRtt;
        localAudioPacketsLost = statistics.localAudioPacketsLost;
        localAudioPacketsLostrate = statistics.localAudioPacketsLostrate;
        localAudioBps = statistics.localAudioBps;
        localAudioRtt = statistics.localAudioRtt;

        remoteWidth = statistics.remoteWidth;
        remoteHeight = statistics.remoteHeight;
        remoteFps = statistics.remoteFps;
        remoteVideoPacketsLost = statistics.remoteVideoPacketsLost;
        remoteVideoPacketsLostrate = statistics.remoteVideoPacketsLostrate;
        remoteVideoBps = statistics.remoteVideoBps;
        remoteAudioPacketsLost = statistics.remoteAudioPacketsLost;
        remoteAudioPacketsLostrate = statistics.remoteAudioPacketsLostrate;
        remoteAudioBps = statistics.remoteAudioBps;
    }

    public String getStreamId() {
        return streamId;
    }

    public int getLocalCaptureWidth() {
        return localCaptureWidth;
    }

    public int getLocalCaptureHeight() {
        return localCaptureHeight;
    }

    public int getLocalCaptureFps() {
        return localCaptureFps;
    }

    public int getLocalEncodedWidth() {
        return localEncodedWidth;
    }

    public int getLocalEncodedHeight() {
        return localEncodedHeight;
    }

    public int getLocalEncodedFps() {
        return localEncodedFps;
    }

    public int getLocalVideoActualBps() {
        return localVideoActualBps;
    }

    public int getLocalVideoTargetBps() {
        return localVideoTargetBps;
    }

    public int getLocalVideoPacketsLost() {
        return localVideoPacketsLost;
    }

    public int getLocalVideoPacketsLostrate() {
        return localVideoPacketsLostrate;
    }

    public int getLocalVideoRtt() {
        return localVideoRtt;
    }

    public int getLocalAudioPacketsLost() {
        return localAudioPacketsLost;
    }

    public int getLocalAudioPacketsLostrate() {
        return localAudioPacketsLostrate;
    }

    public int getLocalAudioBps() {
        return localAudioBps;
    }

    public int getLocalAudioRtt() {
        return localAudioRtt;
    }

    public int getRemoteWidth() {
        return remoteWidth;
    }

    public int getRemoteHeight() {
        return remoteHeight;
    }

    public int getRemoteFps() {
        return remoteFps;
    }

    public int getRemoteVideoPacketsLost() {
        return remoteVideoPacketsLost;
    }

    public int getRemoteVideoPacketsLostrate() {
        return remoteVideoPacketsLostrate;
    }

    public int getRemoteVideoBps() {
        return remoteVideoBps;
    }

    public int getRemoteAudioPacketsLost() {
        return remoteAudioPacketsLost;
    }

    public int getRemoteAudioPacketsLostrate() {
        return remoteAudioPacketsLostrate;
    }

    public int getRemoteAudioBps() {
        return remoteAudioBps;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("statistic:");
        builder.append("\n\tstreamId: " + streamId);
        builder.append("\n\tlocalCaptureWidth: " + localCaptureWidth);
        builder.append("\n\tlocalCaptureHeight: " + localCaptureHeight);
        builder.append("\n\tlocalCaptureFps: " + localCaptureFps);
        builder.append("\n\tlocalEncodedWidth: " + localEncodedWidth);
        builder.append("\n\tlocalEncodedHeight: " + localEncodedHeight);
        builder.append("\n\tlocalEncodedFps: " + localEncodedFps);
        builder.append("\n\tlocalVideoActualBps: " + localVideoActualBps);
        builder.append("\n\tlocalVideoTargetBps: " + localVideoTargetBps);
        builder.append("\n\tlocalVideoPacketsLost: " + localVideoPacketsLost);
        builder.append("\n\tlocalVideoPacketsLostrate: " + localVideoPacketsLostrate);
        builder.append("\n\tlocalVideoRtt: " + localVideoRtt);
        builder.append("\n\tlocalAudioPacketsLost: " + localAudioPacketsLost);
        builder.append("\n\tlocalAudioPacketsLostrate: " + localAudioPacketsLostrate);
        builder.append("\n\tlocalAudioBps: " + localAudioBps);
        builder.append("\n\tlocalAudioRtt: " + localAudioRtt);
        builder.append("\n\tremoteWidth: " + remoteWidth);
        builder.append("\n\tremoteHeight: " + remoteHeight);
        builder.append("\n\tremoteFps: " + remoteFps);
        builder.append("\n\tremoteVideoPacketsLost: " + remoteVideoPacketsLost);
        builder.append("\n\tremoteVideoPacketsLostrate: " + remoteVideoPacketsLostrate);
        builder.append("\n\tremoteVideoBps: " + remoteVideoBps);
        builder.append("\n\tremoteAudioPacketsLost: " + remoteAudioPacketsLost);
        builder.append("\n\tremoteAudioPacketsLostrate: " + remoteAudioPacketsLostrate);
        builder.append("\n\tremoteAudioBps: " + remoteAudioBps);
        return builder.toString();
    }
}
