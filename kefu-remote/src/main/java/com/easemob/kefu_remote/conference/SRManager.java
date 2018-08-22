package com.easemob.kefu_remote.conference;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.easemob.kefu_remote.RemoteApp;
import com.easemob.kefu_remote.utils.VMLog;
import com.superrtc.util.BitmapUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 屏幕捕获管理类
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP) public class SRManager {
    public static final int RECORD_REQUEST_CODE = 1000;

    private static SRManager instance;
    private Activity activity;

    // 截取图片回调接口
    private ScreenShortCallback screenShortCallback;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaProjectionStopCallback stopCallback;
    // 虚拟现实，主要用来录屏实现
    private VirtualDisplay virtualDisplay;
    // 录屏获取图片
    private ImageReader imageReader;
    // 录屏获取视频
    private MediaRecorder mediaRecorder;

    // 当前状态
    private boolean isRunning = false;

    // 用来获取设备宽高等
    private Display screenDisplay;
    private int width = 720;
    private int height = 1080;
    private int density = 1;

    // 文件保存路径
    private String savePath;

    private SRManager() {
    }

    public static SRManager getInstance() {
        if (instance == null) {
            instance = new SRManager();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Activity activity, android.support.v4.app.Fragment fragment) {
        this.activity = activity;
        projectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (projectionManager != null && !isRunning) {
            if (fragment == null){
                activity.startActivityForResult(projectionManager.createScreenCaptureIntent(), SRManager.RECORD_REQUEST_CODE);
            }else {
                fragment.startActivityForResult(projectionManager.createScreenCaptureIntent(), SRManager.RECORD_REQUEST_CODE);
            }
        }
    }

    /**
     * 开始捕获屏幕，此种方式会回调一张张的 Bitmap
     */
    public boolean startScreenShort() {
        VMLog.i("开始屏幕截图");
        if (mediaProjection != null && !isRunning) {
            createScreenShortVirtualDisplay();
            stopCallback = new MediaProjectionStopCallback();
            mediaProjection.registerCallback(stopCallback, null);
            isRunning = true;
        }
        return isRunning;
    }

    /**
     * 开始屏幕录制，此种方式会保存 mp4 视频文件
     */
    public boolean startScreenRecord() {
        VMLog.i("开始屏幕录制");
        if (mediaProjection != null && !isRunning) {
            createScreenRecordVirtualDisplay();
            stopCallback = new MediaProjectionStopCallback();
            mediaProjection.registerCallback(stopCallback, null);
            mediaRecorder.start();
            isRunning = true;
        }
        return isRunning;
    }

    /**
     * 初始化
     */
    public void initMediaProjection(int resultCode, Intent intent) {
        mediaProjection = projectionManager.getMediaProjection(resultCode, intent);
    }

    /**
     * 初始化保存屏幕录像的参数
     */
    private void initRecorder() {
        VMLog.i("初始化媒体记录器");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(initSavePath());
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
            VMLog.i("媒体记录器 准备 完成");
        } catch (IOException e) {
            VMLog.e("媒体记录器 准备 出错了");
            e.printStackTrace();
        }
    }

    /**
     * 创建一个录屏 VirtualDisplay
     */
    private void createScreenRecordVirtualDisplay() {
        VMLog.i("createScreenRecordVirtualDisplay");

        getScreenInfo();
        initRecorder();

        String virtualDisplayName = "ScreenRecord";
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
        virtualDisplay =
                mediaProjection.createVirtualDisplay(virtualDisplayName, width, height, density, flags, mediaRecorder.getSurface(), null, null);
    }

    //static HandlerThread handlerThread = new HandlerThread("Desktop Thread", Process.THREAD_PRIORITY_URGENT_AUDIO);
    //static Handler workderHandler = null;
    //static{
    //    handlerThread.start();
    //    handlerThread.setPriority(Thread.MAX_PRIORITY);
    //    workderHandler = new Handler(handlerThread.getLooper());
    //}


    /**
     * 初始化读取屏幕截图 ImageReader
     */
    private void initImageReader() {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        imageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);
    }

    /**
     * 创建一个ImageReader VirtualDisplay
     */
    private void createScreenShortVirtualDisplay() {
        VMLog.i("createScreenShortVirtualDisplay");

        getScreenInfo();
        initImageReader();

        String virtualDisplayName = "ScreenShort";
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                | DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;
        virtualDisplay =
                mediaProjection.createVirtualDisplay(virtualDisplayName, width, height, density, flags, imageReader.getSurface(), null, null);
    }

    /**
     * 请求完权限后马上获取有可能为null，可以通过判断is null来重复获取。
     */
    public Bitmap getBitmap() {
        Bitmap bitmap = cutoutFrame();
        if (bitmap == null) {
            getBitmap();
        }
        return bitmap;
    }

    /**
     * 通过底层来获取下一帧的图像
     */
    public Bitmap cutoutFrame() {
        Image image = imageReader.acquireLatestImage();
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height);
    }

    /**
     * 获取屏幕信息
     */
    private void getScreenInfo() {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        density = metrics.densityDpi;
        screenDisplay = activity.getWindowManager().getDefaultDisplay();
        // get width and height
        Point size = new Point();
        screenDisplay.getSize(size);
        width = size.x;
        height = size.y;
    }

    private long oldTime = 0;

    /**
     * 图片变化监听
     */
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override public void onImageAvailable(ImageReader reader) {
            Log.i("info","test123 onImageAvailable");
            Image image = reader.acquireLatestImage();
            long currTime = System.currentTimeMillis();
            //            VMLog.d("捕获图片有效回调 %d", currTime - oldTime);
            if (currTime - oldTime > 100) {
                oldTime = currTime;
                Bitmap bitmap = null;
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;
                    // create bitmap
                    bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    Matrix matrix = new Matrix();
                    float s = (float) 1 / BitmapUtil.getZoomScale(width, height, 800);
                    matrix.postScale(s, s);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    if (screenShortCallback != null) {
                        screenShortCallback.onBitmap(bitmap);
                    }
                }
            }
            if (image != null) {
                image.close();
            }
        }
    }

    /**
     * 初始化保存屏幕录像的路径
     */
    public String initSavePath() {
        StringBuilder builder = new StringBuilder();
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/ScreenRecord" + "/";
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    path = RemoteApp.getInstance().getContext().getFilesDir().getPath() + "/";
                }
            }
        } else {
            path = RemoteApp.getInstance().getContext().getFilesDir().getPath() + "/";
        }
        builder.append(path);
        builder.append(System.currentTimeMillis() + ".mp4");
        savePath = builder.toString();
        return savePath;
    }

    /**
     * 屏幕捕获停止回调
     */
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override public void onStop() {
            stop();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 停止捕获
     */
    public boolean stop() {
        if (!isRunning) {
            return false;
        }
        isRunning = false;
        if (projectionManager != null) {
            projectionManager = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder = null;
        }
        if (imageReader != null) {
            imageReader.setOnImageAvailableListener(null, null);
            imageReader.close();
            imageReader = null;
        }
        if (mediaProjection != null) {
            if (stopCallback != null) {
                mediaProjection.unregisterCallback(stopCallback);
                stopCallback = null;
            }
            mediaProjection.stop();
            mediaProjection = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        return true;
    }

    /**
     * 设置屏幕捕获回调接口
     */
    public void setScreenShortCallback(ScreenShortCallback callback) {
        screenShortCallback = callback;
    }

    /**
     * 屏幕捕获回调接口
     */
    public interface ScreenShortCallback {
        void onBitmap(Bitmap bitmap);
    }
}
