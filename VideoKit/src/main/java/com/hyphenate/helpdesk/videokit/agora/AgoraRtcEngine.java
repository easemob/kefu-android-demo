package com.hyphenate.helpdesk.videokit.agora;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ScreenCaptureParameters;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtc2.video.WatermarkOptions;



public class AgoraRtcEngine {

    private final static String TAG = "AgoraRtcEngine";

    public static final int RENDER_MODE_HIDDEN = 1;
    public static final int RENDER_MODE_FIT = 2;
    public static final int RENDER_MODE_ADAPTIVE = 3;
    public static final int RENDER_MODE_FILL = 4;

    private RtcEngine mEngine;
    private Context mContext;
    private int renderMode;
    private int mirrorMode;
    private int mProfile;
    private int mRole;
    private VideoEncoderConfiguration mConfig;
    private String mWatermarkUrl = "";
    private WatermarkOptions mOptions;
    private VideoEncoderConfiguration.VideoDimensions mVideoEncodingDimension;
    private IRtcEngineEventHandler mEngineEventHandler;
    private boolean mIsOpenScreenCapturePaused;

    private AgoraRtcEngine(Context context, String appId, IRtcEngineEventHandler handler,
                           int profile, int role, VideoEncoderConfiguration config,
                           String watermarkUrl, WatermarkOptions options, VideoEncoderConfiguration.VideoDimensions videoEncodingDimension,
                           int renderMode, int mirrorMode){
        if (handler == null){
            throw new RuntimeException("IAgoraRtcEngineEventHandler is null.");
        }
        this.mContext = context;
        this.renderMode = renderMode;
        this.mirrorMode = mirrorMode;

        if (mProfile == -1){
            mProfile = Constants.CHANNEL_PROFILE_CLOUD_GAMING;
        }else {
            this.mProfile = profile;
        }

        this.mConfig = config;
        this.mWatermarkUrl = watermarkUrl;
        this.mOptions = options;
        if (mRole == 0){
            mRole = Constants.CLIENT_ROLE_BROADCASTER;
        }else {
            this.mRole = role;
        }


        if(videoEncodingDimension == null){
            this.mVideoEncodingDimension = VideoEncoderConfiguration.VD_640x360;
        }else {
            this.mVideoEncodingDimension = videoEncodingDimension;
        }

        mEngineEventHandler = handler;

        RtcEngineConfig configs = new RtcEngineConfig();
        configs.mContext = context.getApplicationContext();
        configs.mAppId = appId;
        configs.mChannelProfile = Constants.CHANNEL_PROFILE_CLOUD_GAMING;
        configs.mEventHandler = mEngineEventHandler;
        configs.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);

        try {
            // // reason：5 --> 远端用户禁用 6 --> 远端用户恢复
            // mEngine = RtcEngine.create(context.getApplicationContext(), appId, mEngineEventHandler);
            mEngine = RtcEngine.create(configs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startScreenCapture(){
        ScreenCaptureParameters screenCaptureParameters = new ScreenCaptureParameters();
        screenCaptureParameters.captureAudio = true;
        screenCaptureParameters.captureVideo = true;
        ScreenCaptureParameters.VideoCaptureParameters videoCaptureParameters = new ScreenCaptureParameters.VideoCaptureParameters();
        screenCaptureParameters.videoCaptureParameters = videoCaptureParameters;
        int i = mEngine.startScreenCapture(screenCaptureParameters);
        Log.e(TAG,"startScreenCapture = "+i);

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishScreenCaptureVideo = true;
        options.publishCameraTrack = false;
        int i1 = mEngine.updateChannelMediaOptions(options);
        Log.e(TAG,"startScreenCapture updateChannelMediaOptions = "+i1);

    }


    public void stopScreenCapture(){
        mEngine.stopScreenCapture();
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishScreenCaptureVideo = false;
        options.publishCameraTrack = true;
        int i = mEngine.updateChannelMediaOptions(options);
        Log.e(TAG,"stopScreenCapture updateChannelMediaOptions = "+i);
    }

    public boolean isOpenScreenCapturePaused(){
        return mIsOpenScreenCapturePaused;
    }
    public void screenCaptureResumed() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishScreenCaptureVideo = true;
        options.publishCameraTrack = false;
        int i1 = mEngine.updateChannelMediaOptions(options);
        if (i1 == 0){
            mIsOpenScreenCapturePaused = false;
        }
        Log.e(TAG,"screenCaptureResumed updateChannelMediaOptions = "+i1);
    }

    public void screenCapturePaused() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.publishScreenCaptureVideo = false;
        options.publishCameraTrack = true;
        int i1 = mEngine.updateChannelMediaOptions(options);
        Log.e(TAG,"screenCapturePaused updateChannelMediaOptions = "+i1);
        if (i1 == 0){
            mIsOpenScreenCapturePaused = true;
        }
    }

    /**
     * 截图
     * @param uid uid
     * @param filePath 路径
     * @return 路径
     */
    public int takeSnapshot(int uid, String filePath){
        return mEngine.takeSnapshot(uid, filePath);
    }

    /**
     * 创建 RendererView
     *
     * 请在主线程调用该方法
     * @return SurfaceView
     */
    public SurfaceView createRendererView(){
        return RtcEngine.CreateRendererView(mContext.getApplicationContext());
    }

    public TextureView createTextureView(){
        return RtcEngine.CreateTextureView(mContext.getApplicationContext());
    }

    /**
     * 检查设备是否支持打开闪光灯
     * @return boolean
     */
    public boolean isCameraTorchSupported(){
        return mEngine.isCameraTorchSupported();
    }

    /**
     * 设置是否打开闪光灯
     * @param isOn true / false
     */
    public void setCameraTorchOn(boolean isOn){
        mEngine.setCameraTorchOn(isOn);
    }

    /**
     * 检测设备是否支持手动对焦功能
     * @return boolean
     */
    public boolean isCameraFocusSupported(){
        return mEngine.isCameraFocusSupported();
    }

    /**
     * 设置手动对焦位置，并触发对焦
     * @param positionX x方向位置
     * @param positionY y方向位置
     */
    public void setCameraFocusPositionInPreview(float positionX, float positionY){
        mEngine.setCameraFocusPositionInPreview(positionX, positionY);
    }

    /**
     * 创建 RendererView
     *
     * 请在主线程调用该方法
     * @param context (Android Activity) 的上下文。
     * @return SurfaceView
     */
    public SurfaceView createRendererView(Context context){
        return RtcEngine.CreateRendererView(context);
    }

    /**
     * 使用 uid 加入频道
     *
     * 该方法不支持相同的用户重复加入同一个频道，如果想要从不同的设备同时接入同一个频道，请确保每个设备上使用的 UID 是不同的
     * 请确保用于生成 Token 的 App ID 和创建 IRtcEngine 对象时用的 App ID 一致
     * @param token 在 app 服务端生成的用于鉴权的 Token
     * @param channelName 要加入的渠道名
     * @param optionalUid 用户 ID，32 位无符号整数。建议设置范围：1 到 (232-1)，并保证唯一性。如果不指定（即设为 0），SDK 会自动分配一个， 并在 onJoinChannelSuccess 回调方法中返回，App 层必须记住该返回值并维护，SDK 不对该返回值进行维护
     * @return 0：调用成功，< 0：方法调用失败
     * 调用失败将返回如下：
     * ERR_INVALID_ARGUMENT(-2)
     * ERR_NOT_READY(-3)
     * ERR_REFUSED(-5)
     * ERR_NOT_INITIALIZED(-7)
     * ERR_JOIN_CHANNEL_REJECTED(-17)：加入频道被拒绝。由于 SDK 不支持用户重复加入同一个 RtcChannel 频道， 当已经加入某个 RtcChannel 频道的用户再次调用该 RtcChannel 对象的加入频道方法时，会返回此错误码
     */
    public int joinChannel(String token, String channelName, int optionalUid){
        if (mConfig == null){
            mConfig = new VideoEncoderConfiguration(
                    getVideoEncodingDimensionObject(),
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            );
        }

        mEngine.setChannelProfile(mProfile);
        mEngine.setClientRole(mRole);
        mEngine.enableVideo();

        mEngine.setVideoEncoderConfiguration(mConfig);
        //mVideoEncodingDimension

        if (mOptions != null){
            // 添加视频水印
            mEngine.addVideoWatermark(mWatermarkUrl, mOptions);
        }


        /*ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeAudio = true;
        option.autoSubscribeVideo = true;
        option.publishMicrophoneTrack = true;
        option.publishCameraTrack = true;*/

        // 使用 Token 加入频道。
        // return mEngine.joinChannel(token, channelName,"",optionalUid);
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        options.autoSubscribeVideo = true;
        options.autoSubscribeAudio = true;
        options.publishCameraTrack = true;
        options.publishMicrophoneTrack = true;




        return mEngine.joinChannel(token, channelName, optionalUid, options);
        //return mEngine.joinChannel(token, channelName, "", optionalUid, option);
    }

    /**
     *  使用 uid 加入频道
     *
     * 该方法不支持相同的用户重复加入同一个频道，如果想要从不同的设备同时接入同一个频道，请确保每个设备上使用的 UID 是不同的
     * 请确保用于生成 Token 的 App ID 和创建 IRtcEngine 对象时用的 App ID 一致
     * @param token 在 app 服务端生成的用于鉴权的 Token
     * @param channelName 要加入的渠道名
     * @param optionalInfo 开发者需加入的任何附加信息。一般可设置为空字符串，或频道相关信息。该信息不会传递给频道内的其他用户。
     * @param optionalUid 用户 ID，32 位无符号整数。建议设置范围：1 到 (232-1)，并保证唯一性。如果不指定（即设为 0），SDK 会自动分配一个， 并在 onJoinChannelSuccess 回调方法中返回，App 层必须记住该返回值并维护，SDK 不对该返回值进行维护
     * @param options 频道媒体设置选项：ChannelMediaOptions
     * @return 0：调用成功，< 0：方法调用失败
     * 调用失败将返回如下：
     * ERR_INVALID_ARGUMENT(-2)
     * ERR_NOT_READY(-3)
     * ERR_REFUSED(-5)
     * ERR_NOT_INITIALIZED(-7)
     * ERR_JOIN_CHANNEL_REJECTED(-17)：加入频道被拒绝。由于 SDK 不支持用户重复加入同一个 RtcChannel 频道， 当已经加入某个 RtcChannel 频道的用户再次调用该 RtcChannel 对象的加入频道方法时，会返回此错误码
     */
    public int joinChannel(String token, String channelName, String optionalInfo, int optionalUid, ChannelMediaOptions options){
        if (mConfig == null){
            mConfig = new VideoEncoderConfiguration(
                    getVideoEncodingDimensionObject(),
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            );
        }

        mEngine.setChannelProfile(mProfile);
        mEngine.setClientRole(mRole);
        mEngine.enableVideo();

        mEngine.setVideoEncoderConfiguration(mConfig);

        if (mOptions != null){
            // Setup watermark options
            /*mOptions = new WatermarkOptions();
            int size = getVideoEncodingDimensionObject().width / 6;
            int height = getVideoEncodingDimensionObject().height;
            mOptions.positionInPortraitMode = new WatermarkOptions.Rectangle(10, height / 2, size, size);
            mOptions.positionInLandscapeMode = new WatermarkOptions.Rectangle(10, height / 2, size, size);
            mOptions.visibleInPreview = true;*/
            // 添加视频水印
            mEngine.addVideoWatermark(mWatermarkUrl, mOptions);
        }
        // return mEngine.joinChannel(token, channelName, optionalInfo, optionalUid, options);
        return mEngine.joinChannel(token, channelName, optionalUid,new ChannelMediaOptions());
    }

    /*private VideoEncoderConfiguration.VideoDimensions getVideoEncodingDimensionObject() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return new VideoEncoderConfiguration.VideoDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }*/

    private VideoEncoderConfiguration.VideoDimensions getVideoEncodingDimensionObject() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return new VideoEncoderConfiguration.VideoDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     *更新本地视图显示模式
     *
     * 初始化本地用户视图后，你可以调用该方法更新本地用户视图的渲染和镜像模式。该方法只影响本地用户看到的视频画面，不影响本地发布视频
     * 注意：请在调用 setupLocalVideo 方法初始化本地视图后，调用该方法。你可以在通话中多次调用该方法，多次更新本地用户视图的显示模式。
     * @param renderMode renderMode	本地视图的渲染模式。
     * RENDER_MODE_HIDDEN(1): 优先保证视窗被填满。视频尺寸等比缩放，直至整个视窗被视频填满。如果视频长宽与显示窗口不同，多出的视频将被截掉。
     * RENDER_MODE_FIT(2): 优先保证视频内容全部显示。视频尺寸等比缩放，直至视频窗口的一边与视窗边框对齐。如果视频长宽与显示窗口不同，视窗上未被填满的区域将被涂黑。
     * RENDER_MODE_ADAPTIVE(3): 已废弃，不再推荐使用。
     * RENDER_MODE_FILL(4): 视频尺寸进行缩放和拉伸以充满显示视窗。
     * @param mirrorMode 本地视图的镜像模式。
     * VIDEO_MIRROR_AUTO(0): 默认的镜像模式（SDK 决定镜像模式）。如果你使用前置摄像头，默认启动本地视图镜像模式；如果你启用后置摄像头，默认关闭本地视图镜像模式。
     * VIDEO_MIRROR_MODE_ENABLED(1): 开启镜像模式。
     * VIDEO_MIRROR_MODE_DISABLED(2): 关闭镜像模式。
     * @return 0：方法调用成功。< 0：方法调用失败。
     */
    public int setLocalRenderMode(int renderMode, int mirrorMode){
        return mEngine.setLocalRenderMode(renderMode, mirrorMode);
    }

    /**
     *  初始化本地视图
     *
     * 该方法初始化本地视图并设置本地用户视频显示信息，只影响本地用户看到的视频画面，不影响本地发布视频。
     * 调用该方法绑定本地视频流的显示视窗（View），并设置本地用户视图的渲染模式和镜像模式。
     * @param surfaceView 详细说明查看setLocalRenderMode()方法注解
     * @param renderMode 详细说明查看setLocalRenderMode()方法注解
     * @param uid uid
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setupLocalVideo(View surfaceView, int renderMode, int uid){
        // VideoCanvas.RENDER_MODE_HIDDEN
        // View view, int renderMode, int uid
        return mEngine.setupLocalVideo(new VideoCanvas(surfaceView, renderMode, uid , mirrorMode));
    }

    /**
     * 初始化本地视图
     *
     * @param surfaceView 通过createRendererView()方法获取
     * @param uid uid
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setupLocalVideo(View surfaceView, int uid){
        // VideoCanvas.RENDER_MODE_HIDDEN
        // View view, int renderMode, int uid
        return mEngine.setupLocalVideo(new VideoCanvas(surfaceView, renderMode, uid));
    }

    /**
     * 初始化远端用户视图
     *
     * 该方法绑定远端用户和显示视图，并设置远端用户视图在本地显示时的渲染模式和镜像模式，只影响本地用户看到的视频画面。
     * 如果 App 不能事先知道对方的用户 ID，可以在 APP 收到 onUserJoined 事件时设置。
     * 如果启用了视频录制功能，视频录制服务会做为一个哑客户端加入频道，因此其他客户端也会收到它的 onUserJoined 事件，
     * App 不应给它绑定视图（因为它不会发送视频流），如果 App 不能识别哑客户端，可以在 onFirstRemoteVideoDecoded 事件时再绑定视图。
     * 解除某个用户的绑定视图可以把 view 设置为空。退出频道后，SDK 会把远程用户的绑定关系清除掉。
     * 注意：请在主线程调用该方法。如果你希望在通话中更新远端用户的渲染或镜像模式，请使用 setRemoteRenderMode 方法
     * @param surfaceView 视图，通过createRendererView()方法获取
     * @param renderMode 渲染模式，详细说明查看setRenderMode()方法注解
     * @param uid uid
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setupRemoteVideo(View surfaceView, int renderMode, int uid){
        return mEngine.setupRemoteVideo(new VideoCanvas(surfaceView, renderMode, uid));
    }

    /**
     * 初始化远端用户视图
     *
     * 该方法绑定远端用户和显示视图，并设置远端用户视图在本地显示时的渲染模式和镜像模式，只影响本地用户看到的视频画面。
     * 如果 App 不能事先知道对方的用户 ID，可以在 APP 收到 onUserJoined 事件时设置。
     * 如果启用了视频录制功能，视频录制服务会做为一个哑客户端加入频道，因此其他客户端也会收到它的 onUserJoined 事件，
     * App 不应给它绑定视图（因为它不会发送视频流），如果 App 不能识别哑客户端，可以在 onFirstRemoteVideoDecoded 事件时再绑定视图。
     * 解除某个用户的绑定视图可以把 view 设置为空。退出频道后，SDK 会把远程用户的绑定关系清除掉。
     * 注意：请在主线程调用该方法。如果你希望在通话中更新远端用户的渲染或镜像模式，请使用 setRemoteRenderMode 方法
     * @param surfaceView 视图，通过createRendererView()方法获取
     * @param uid uid
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setupRemoteVideo(View surfaceView, int uid){
        return mEngine.setupRemoteVideo(new VideoCanvas(surfaceView, renderMode, uid));
    }

    /**
     * 初始化远端用户视图
     *
     * 如果 App 不能事先知道对方的用户 ID，可以在 APP 收到 onUserJoined 事件时设置。
     * 如果启用了视频录制功能，视频录制服务会做为一个哑客户端加入频道，因此其他客户端也会收到它的 onUserJoined 事件，
     * App 不应给它绑定视图（因为它不会发送视频流），如果 App 不能识别哑客户端，可以在 onFirstRemoteVideoDecoded 事件时再绑定视图。
     * 解除某个用户的绑定视图可以把 view 设置为空。退出频道后，SDK 会把远程用户的绑定关系清除掉。
     * 注意：请在主线程调用该方法。如果你希望在通话中更新远端用户的渲染或镜像模式，请使用 setRemoteRenderMode 方法
     * 如果你希望在通话中更新远端用户的渲染或镜像模式，请使用 setRemoteRenderMode 方法
     * @param videoCanvas VideoCanvas
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setupRemoteVideo(VideoCanvas videoCanvas){
        return mEngine.setupRemoteVideo(videoCanvas);
    }


    /**
     * 更新远端视图显示模式
     *
     * @param uid uid
     * @param renderMode 详细说明查看setRenderMode()方法注解
     * @param mirrorMode 详细说明查看setMirrorMode()方法
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int setRemoteRenderMode(int uid, int renderMode, int mirrorMode){
        return mEngine.setRemoteRenderMode(uid, renderMode, mirrorMode);
    }


    /**
     * 启用视频模块
     *
     * 该方法用于打开视频模式。可以在加入频道前或者通话中调用，在加入频道前调用，则自动开启视频模式，在通话中调用则由音频模式切换为视频模式。调用 disableVideo 方法可关闭视频模式。
     * 成功调用该方法后，远端会触发 onUserEnableVideo(true) 回调
     * 该方法设置的是内部引擎为开启状态，在频道内和频道外均可调用，且在 leaveChannel 后仍然有效。
     * 该方法重置整个引擎，响应速度较慢，因此 Agora 建议使用如下方法来控制视频模块：
     * enableLocalVideo：是否启动摄像头采集并创建本地视频流
     * muteLocalVideoStream：是否发布本地视频流
     * muteRemoteVideoStream：是否接收并播放远端视频流
     * muteAllRemoteVideoStreams：是否接收并播放所有远端视频流
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int enableVideo(){
        return mEngine.enableVideo();
    }

    /**
     * 关闭视频模块
     *
     * 该方法用于关闭视频。可以在加入频道前或者通话中调用，在加入频道前调用，则自动开启纯音频模式，在通话中调用则由视频模式切换为纯音频频模式。调用 enableVideo 方法可开启视频模式。
     * 成功调用该方法后，远端会触发 onUserEnableVideo(false) 回调。
     * 该方法设置的是内部引擎为禁用状态，在频道内和频道外均可调用，且在 leaveChannel 后仍然有效。
     * 该方法重置整个引擎，响应速度较慢，因此 建议使用如下方法来控制视频模块：
     * enableLocalVideo：是否启动摄像头采集并创建本地视频流
     * muteLocalVideoStream：是否发布本地视频流
     * muteRemoteVideoStream：是否接收并播放远端视频流
     * muteAllRemoteVideoStreams：是否接收并播放所有远端视频流
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int disableVideo(){
        return mEngine.disableVideo();
    }


    /**
     * 启用音频模块（默认为开启状态）
     *
     * 该方法设置的是音频模块为开启状态，在频道内和频道外均可调用，且在 leaveChannel 后仍然有效
     * 该方法开启整个音频模块，响应速度较慢，因此建议使用如下方法来控制音频模块：
     * enableLocalAudio：是否启动麦克风采集并创建本地音频流。
     * muteLocalAudioStream：是否发布本地音频流。
     * muteRemoteAudioStream：是否接收并播放远端音频流。
     * muteAllRemoteAudioStreams：是否接收并播放所有远端音频流。
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int enableAudio(){
        return mEngine.enableAudio();
    }

    /**
     * 关闭音频模块
     *
     * 该方法设置的是音频模块为禁用状态，在频道内和频道外均可调用，且在 leaveChannel 后仍然有效
     * 该方法关闭整个音频模块，响应速度较慢，因此建议使用如下方法来控制音频模块
     * enableLocalAudio：是否启动麦克风采集并创建本地音频流。
     * muteLocalAudioStream：是否发布本地音频流。
     * muteRemoteAudioStream：是否接收并播放远端音频流。
     * muteAllRemoteAudioStreams：是否接收并播放所有远端音频流。
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int disableAudio(){
        return mEngine.disableAudio();
    }


    /**
     * 开启/关闭本地音频采集。
     *
     * 当 app 加入频道时，它的语音功能默认是开启的。该方法可以关闭或重新开启本地语音，即停止或重新开始本地音频采集
     * 该方法不影响接收远端音频流，enableLocalAudio(false) 适用于只听不发的用户场景。
     * 语音功能关闭或重新开启后，会收到回调 onLocalAudioStateChanged ，并报告 LOCAL_AUDIO_STREAM_STATE_STOPPED(0) 或 LOCAL_AUDIO_STREAM_STATE_CAPTURING(1)
     * 该方法与 muteLocalAudioStream 的区别在于：
     * enableLocalAudio 开启或关闭本地语音采集及处理。使用 enableLocalAudio 关闭或开启本地采集后，本地听远端播放会有短暂中断。
     * muteLocalAudioStream 停止或继续发送本地音频流。
     * @param enabled 是否开启
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int enableLocalAudio(boolean enabled){
        return mEngine.enableLocalAudio(enabled);
    }

    /**
     * 取消或恢复订阅指定远端用户的音频流
     *
     * 该方法需要在加入频道后调用
     * @param uid 指定用户的用户 ID
     * @param muted 是否取消订阅指定远端用户的音频流，true：取消订阅。false：（默认）订阅
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int muteRemoteAudioStream(int uid, boolean muted){
        return mEngine.muteRemoteAudioStream(uid, muted);
    }

    /**
     * 取消或恢复订阅所有远端用户的音频流
     *
     * 成功调用该方法后，本地用户会取消或恢复订阅所有远端用户的音频流，包括在调用该方法后加入频道的用户的音频流
     * @param muted 是否取消订阅所有远端用户的音频流。true: 取消订阅。false:（默认）订阅
     * @return 0: 方法调用成功。< 0: 方法调用失败
     */
    public int muteAllRemoteAudioStreams(boolean muted){
        return mEngine.muteAllRemoteAudioStreams(muted);
    }

    /**
     * 取消或恢复发布本地音频流
     *
     * 该方法仅设置用户在 RtcChannel 频道中的音频发布状态。
     * 成功调用该方法后，远端会触发 onRemoteAudioStateChanged 回调。
     * 同一时间，本地的音视频流只能发布到一个频道。如果你创建了多个频道，请确保你只在一个频道中调用 muteLocalAudioStream(false)，否则方法会调用失败并返回 -5 (ERR_REFUSED)。
     * @param muted 是否取消发布本地音频流，true：取消发布，false：发布
     * @return 0: 方法调用成功。< 0: 方法调用失败。-5 (ERR_REFUSED)：调用被拒绝
     */
    public int muteLocalAudioStream(boolean muted){
        return mEngine.muteLocalAudioStream(muted);
    }

    /**
     * 开启/关闭扬声器播放
     *
     * 你可以调用 setEnableSpeakerphone 切换当前的音频路由。 成功切换音频路由后，SDK 会触发 onAudioRouteChanged 回调提示音频路由已更改。
     * 该方法只设置用户在当前频道内使用的音频路由，不会影响 SDK 默认的音频路由。 如果用户离开当前频道并加入新的频道，则用户还是会使用 SDK 默认的音频路由
     * 该方法需要在 joinChannel 后调用。
     * 如果用户使用了蓝牙耳机、有线耳机等外接音频播放设备，则该方法的设置无效，音频只会通过外接设备播放。当有多个外接设备时，音频会通过最后一个接入的设备播放。
     * @param enabled 设置是否开启扬声器播放，true：开启。音频路由为扬声器。false：关闭。音频路由为听筒。
     * @return 0：方法调用成功。< 0：方法调用失败
     */
    public int setEnableSpeakerphone(boolean enabled){
        return mEngine.setEnableSpeakerphone(enabled);
    }


    /**
     * 取消或恢复发布本地视频流。
     *
     * 成功调用该方法后，远端会触发 onUserMuteVideo 回调。
     * 同一时间，本地的音视频流只能发布到一个频道。如果你创建了多个频道，请确保你只在一个频道中调用 muteLocalVideoStream(false)， 否则方法会调用失败并返回 -5 (ERR_REFUSED)。
     * 该方法不会改变视频采集设备的使用状态。该方法的调用是否生效受 joinChannel 和 setClientRole 方法的影响
     * @param muted 是否取消发布本地视频流。true：取消发布。false：发布。
     * @return 0：方法调用成功。< 0：方法调用失败。-5 (ERR_REFUSED)：调用被拒绝
     */
    public int muteLocalVideoStream(boolean muted){
        return mEngine.muteLocalVideoStream(muted);
    }

    /**
     * 切换前置/后置摄像头。
     *
     * 该方法需要在相机启动（如通过调用 startPreview 或 joinChannel 实现）后调用。
     * @return 0: 方法调用成功。< 0: 方法调用失败。
     */
    public int switchCamera(){
        return mEngine.switchCamera();
    }

    /**
     * 离开当前频道
     *
     * 成功调用该方法离开频道后，会触发如下回调：
     * 本地：onLeaveChannel.
     * 远端：通信场景下的用户和直播场景下的主播离开频道后，触发 onUserOffline。
     * @return 0：方法调用成功。< 0：方法调用失败
     */
    public int leaveChannel(){
        return mEngine.leaveChannel();
    }


    // 分享桌面
    /*public void shareWindows(ScreenSharingClient client, Context context, String appId, String token, String channelName, int uid, VideoEncoderConfiguration configurations){
        client.start(context, appId, token,
                channelName, uid, configurations);
    }*/


    public static AgoraRtcEngineConfigure builder(){
        return new AgoraRtcEngineConfigure();
    }

    /**
     * 销毁释放资源
     */
    public void onDestroy() {
        if (mEngine != null){
            mEngine.leaveChannel();
            // 分享销毁
            /*if (isSharing) {
                mSSClient.stop(getContext());
            }*/
            if (mEngineEventHandler != null){
                mEngine.removeHandler(mEngineEventHandler);
            }
            RtcEngine.destroy();

        }

        mIsOpenScreenCapturePaused = false;
        mEngineEventHandler = null;
        mContext = null;
    }

    public static class AgoraRtcEngineConfigure{
        private int mProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        private int mRole;
        private int renderMode = VideoCanvas.RENDER_MODE_FIT;
        private int mirrorMode = 0;

        private VideoEncoderConfiguration mConfig;
        private String mWatermarkUrl = "";
        private WatermarkOptions mOptions;
        private VideoEncoderConfiguration.VideoDimensions mVideoEncodingDimension;

        public AgoraRtcEngine build(Context context, String appId, IRtcEngineEventHandler handler){
            return new AgoraRtcEngine(context, appId, handler, mProfile, mRole, mConfig,
                    mWatermarkUrl, mOptions, mVideoEncodingDimension, renderMode, mirrorMode);
        }

        /**
         * 设置频道场景
         * @param profile CHANNEL_PROFILE_COMMUNICATION，CHANNEL_PROFILE_LIVE_BROADCASTING，CHANNEL_PROFILE_GAME
         * CHANNEL_PROFILE_COMMUNICATION(0)：通信场景。该场景下，频道内所有用户都可以发布和接收音、视频流。适用于语音通话、视频群聊等应用场景
         * CHANNEL_PROFILE_LIVE_BROADCASTING(1)：直播场景。该场景有主播和观众两种用户角色，可以通过 setClientRole 设置。主播可以发布和接收音视频流，观众直接接收流。适用于语聊房、视频直播、互动大班课等应用场景
         * CHANNEL_PROFILE_GAME(2)：Agora 不推荐使用
         */
        public void setChannelProfile(int profile){
            this.mProfile = profile;
        }

        /**
         * 设置直播场景下的用户角色
         * @param role role
         * CLIENT_ROLE_BROADCASTER(1)：主播可以发流也可以收流。如果你在频道中设置该角色，SDK 会自动调用
         * CLIENT_ROLE_AUDIENCE(2)：观众只能收流不能发流。如果你在频道中设置该角色，SDK 会自动调用
         */
        public void setClientRole(int role){
            this.mRole = role;
        }

        /**
         * 视频编码像素
         * @param videoEncodingDimension VideoEncoderConfiguration.VideoDimensions指定像素：宽高
         */
        public void setVideoEncodingDimension(VideoEncoderConfiguration.VideoDimensions videoEncodingDimension) {
            this.mVideoEncodingDimension = videoEncodingDimension;
        }


        /**
         * VideoEncoderConfiguration 是视频编码属性的定义
         * @param config VideoEncoderConfiguration
         */
        public void setVideoEncoderConfiguration(VideoEncoderConfiguration config){
            this.mConfig = config;
        }


        /**
         * 添加本地视频水印，该方法将一张 PNG 图片作为水印添加到本地发布的直播视频流上
         * @param watermarkUrl watermarkUrl
         * @param options options
         */
        public void addVideoWatermark(String watermarkUrl, WatermarkOptions options){
            this.mWatermarkUrl = watermarkUrl;
            this.mOptions = options;
        }

        /**
         * 设置视频渲染模式
         * @param renderMode renderMode
         * RENDER_MODE_HIDDEN：优先保证视窗被填满。视频尺寸等比缩放，直至整个视窗被视频填满。如果视频长宽与显示窗口不同，多出的视频将被截掉
         * RENDER_MODE_FIT：优先保证视频内容全部显示。视频尺寸等比缩放，直至视频窗口的一边与视窗边框对齐。如果视频长宽与显示窗口不同，视窗上未被填满的区域将被涂黑
         */
        public void setRenderMode(int renderMode){
            this.renderMode = renderMode;
        }

        /**
         * 设置视频镜像模式
         * @param mirrorMode VIDEO_MIRROR_MODE_AUTO：自动决定镜像模式，VIDEO_MIRROR_MODE_ENABLED：启用镜像模式，VIDEO_MIRROR_MODE_DISABLED：关闭镜像模式
         */
        public void setMirrorMode(int mirrorMode){
            this.mirrorMode = mirrorMode;
        }

    }
}
