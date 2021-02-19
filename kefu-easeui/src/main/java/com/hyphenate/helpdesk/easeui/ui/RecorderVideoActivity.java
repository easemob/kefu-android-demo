package com.hyphenate.helpdesk.easeui.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hyphenate.helpdesk.easeui.util.Utils;
import com.hyphenate.helpdesk.R;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;

public class RecorderVideoActivity extends BaseActivity implements
		OnClickListener, SurfaceHolder.Callback, OnErrorListener,
		OnInfoListener {
	private static final String TAG = "RecorderVideoActivity";
	private final static String CLASS_LABEL = "RecordActivity";
	private PowerManager.WakeLock mWakeLock;
	private ImageView btnStart;
	private ImageView btnStop;
	private MediaRecorder mediaRecorder;
	private VideoView mVideoView;// to display video
	String localPath = "";// path to save recorded video
	private Camera mCamera;
	private int previewWidth = 640;
	private int previewHeight = 480;
	private Chronometer chronometer;
	private int frontCamera = 0; // 0 is back camera，1 is front camera
	private Button btn_switch;
	Parameters cameraParameters = null;
	private SurfaceHolder mSurfaceHolder;
	int defaultVideoFrameRate = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// no title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// full screen
		// translucency mode，used in surface view
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.hd_recorder_activity);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		assert pm != null;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp: RecorderActivity");
		} else {
			mWakeLock = pm.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "myApp: RecorderActivity");
		}

		mWakeLock.acquire();
		initViews();
	}

	private void initViews() {
		btn_switch = (Button) findViewById(R.id.switch_btn);
		btn_switch.setOnClickListener(this);
		btn_switch.setVisibility(View.VISIBLE);
		mVideoView = (VideoView) findViewById(R.id.mVideoView);
		btnStart = (ImageView) findViewById(R.id.recorder_start);
		btnStop = (ImageView) findViewById(R.id.recorder_stop);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		mSurfaceHolder = mVideoView.getHolder();
		mSurfaceHolder.addCallback(this);
//		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
	}

	public void back(View view) {
		releaseRecorder();
		releaseCamera();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWakeLock == null) {
			// keep screen on
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			assert pm != null;
			mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp: RecorderActivity");
			mWakeLock.acquire();
		}
	}

	@SuppressLint("NewApi")
	private boolean initCamera() {
		try {
			if (frontCamera == 0) {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			} else {
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
			}
			Camera.Parameters camParams = mCamera.getParameters();
			mCamera.lock();
			mSurfaceHolder = mVideoView.getHolder();
			mSurfaceHolder.addCallback(this);
//			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			mCamera.setDisplayOrientation(90);

		} catch (RuntimeException ex) {
			EMLog.e("video", "init Camera fail " + ex.getMessage());
			return false;
		}
		return true;
	}

	private void setCameraParameter(Camera camera) {
		if (camera == null) return;
		Camera.Parameters parameters = camera.getParameters();
		//获取相机支持的>=20fps的帧率，用于设置给MediaRecorder
		//因为获取的数值是*1000的，所以要除以1000
		List<int[]> previewFpsRange = parameters.getSupportedPreviewFpsRange();
		for (int[] ints : previewFpsRange) {
			if (ints[0] >= 20000) {
				defaultVideoFrameRate = ints[0] / 1000;
				break;
			}
		}
		//设置聚焦模式
		List<String> focusModes = parameters.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		} else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		//设置预览尺寸,因为预览的尺寸和最终是录制视频的尺寸无关，所以我们选取最大的数值
		//通常最大的是手机的分辨率，这样可以让预览画面尽可能清晰并且尺寸不变形，前提是TextureView的尺寸是全屏或者接近全屏
		// 取消设置预览尺寸，部分手机屏幕显示会变形
//		List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//		parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);
		//缩短Recording启动时间
		parameters.setRecordingHint(true);
		//是否支持影像稳定能力，支持则开启
		if (parameters.isVideoStabilizationSupported())
			parameters.setVideoStabilization(true);
		camera.setParameters(parameters);
	}

	private void handleSurfaceChanged() {
		if (mCamera == null) {
			finish();
			return;
		}
		boolean hasSupportRate = false;
		List<Integer> supportedPreviewFrameRates = mCamera.getParameters()
				.getSupportedPreviewFrameRates();
		if (supportedPreviewFrameRates != null
				&& supportedPreviewFrameRates.size() > 0) {
			Collections.sort(supportedPreviewFrameRates);
			for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
				int supportRate = supportedPreviewFrameRates.get(i);

				if (supportRate == 15) {
					hasSupportRate = true;
				}

			}
			if (hasSupportRate) {
				defaultVideoFrameRate = 15;
			} else {
				defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
			}

		}

		// get all resolutions which camera provide
		List<Camera.Size> resolutionList = Utils.getResolutionList(mCamera);
		if (resolutionList != null && resolutionList.size() > 0) {
			Collections.sort(resolutionList, new Utils.ResolutionComparator());
			Camera.Size previewSize = null;
			boolean hasSize = false;

			// use 60*480 if camera support
			for (int i = 0; i < resolutionList.size(); i++) {
				Size size = resolutionList.get(i);
				if (size != null && size.width == 640 && size.height == 480) {
					previewSize = size;
					previewWidth = previewSize.width;
					previewHeight = previewSize.height;
					hasSize = true;
					break;
				}
			}
			// use medium resolution if camera don't support the above resolution
			if (!hasSize) {
				int mediumResolution = resolutionList.size() / 2;
				if (mediumResolution >= resolutionList.size())
					mediumResolution = resolutionList.size() - 1;
				previewSize = resolutionList.get(mediumResolution);
				previewWidth = previewSize.width;
				previewHeight = previewSize.height;

			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mWakeLock != null) {
			try{
				mWakeLock.release();
			}catch (Exception e){}
			mWakeLock = null;
		}

		releaseRecorder();
		releaseCamera();

		finish();
	}

	@Override
	public void onClick(View view) {
		int i = view.getId();
		if (i == R.id.switch_btn) {
			switchCamera();

		} else if (i == R.id.recorder_start) {// start recording
			if (!startRecording())
				return;
			ToastHelper.show(this, R.string.The_video_to_start);
			btn_switch.setVisibility(View.INVISIBLE);
			btnStart.setVisibility(View.INVISIBLE);
			btnStart.setEnabled(false);
			btnStop.setVisibility(View.VISIBLE);
			chronometer.setBase(SystemClock.elapsedRealtime());
			chronometer.start();

		} else if (i == R.id.recorder_stop) {
			btnStop.setEnabled(false);
			stopRecording();
			btn_switch.setVisibility(View.VISIBLE);
			chronometer.stop();
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);
			new AlertDialog.Builder(this)
					.setMessage(R.string.Whether_to_send)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
								                    int which) {
									dialog.dismiss();
									sendVideo(null);

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
								                    int which) {
									if (localPath != null) {
										File file = new File(localPath);
										if (file.exists())
											file.delete();
									}
									finish();

								}
							}).setCancelable(false).show();

		} else {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	                           int height) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null){
			if(!initCamera()){
				showFailDialog();
				return;
			}

		}
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			setCameraParameter(mCamera);
			mCamera.startPreview();
			handleSurfaceChanged();
		} catch (Exception e1) {
			EMLog.e("video", "start preview fail " + e1.getMessage());
			showFailDialog();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		EMLog.v("video", "surfaceDestroyed");
	}

	public boolean startRecording(){
		if (mediaRecorder == null){
			if(!initRecorder())
				return false;
		}
		try{
			mediaRecorder.setOnInfoListener(this);
			mediaRecorder.setOnErrorListener(this);
			mediaRecorder.start();
		}catch (Exception e){
			return false;
		}
		return true;
	}

	@SuppressLint("NewApi")
	private boolean initRecorder(){
		if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			showNoSDCardDialog();
			return false;
		}

		if (mCamera == null) {
			if(!initCamera()){
				showFailDialog();
				return false;
			}
		}
		mVideoView.setVisibility(View.VISIBLE);
		mCamera.stopPreview();
		mediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		if (frontCamera == 1) {
			mediaRecorder.setOrientationHint(270);
		} else {
			mediaRecorder.setOrientationHint(90);
		}

		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// set resolution, should be set after the format and encoder was set
		mediaRecorder.setVideoSize(previewWidth, previewHeight);
		mediaRecorder.setVideoEncodingBitRate(384 * 1024);
		// set frame rate, should be set after the format and encoder was set
		if (defaultVideoFrameRate != -1) {
			mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
		}
		// set the path for video file
		localPath = PathUtil.getInstance().getVideoPath() + "/"
				+ System.currentTimeMillis() + ".mp4";
		mediaRecorder.setOutputFile(localPath);
		mediaRecorder.setMaxDuration(30000);
		mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public void stopRecording() {
		if (mediaRecorder != null) {
			mediaRecorder.setOnErrorListener(null);
			mediaRecorder.setOnInfoListener(null);
			try {
				mediaRecorder.stop();
			} catch (Exception e) {
				EMLog.e("video", "stopRecording error:" + e.getMessage());
			}
		}
		releaseRecorder();

		if (mCamera != null) {
			mCamera.stopPreview();
			releaseCamera();
		}
	}

	private void releaseRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	protected void releaseCamera() {
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception ignored) {
		}
	}

	@SuppressLint("NewApi")
	public void switchCamera() {

		if (mCamera == null) {
			return;
		}
		if (Camera.getNumberOfCameras() >= 2) {
			btn_switch.setEnabled(false);
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

			switch (frontCamera) {
				case 0:
					mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
					frontCamera = 1;
					break;
				case 1:
					mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
					frontCamera = 0;
					break;
				default:
					break;
			}
			try {
				assert mCamera != null;
				mCamera.lock();
				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(mVideoView.getHolder());
				mCamera.startPreview();
			} catch (IOException e) {
				mCamera.release();
				mCamera = null;
			}
			btn_switch.setEnabled(true);

		}

	}

	MediaScannerConnection msc = null;
	ProgressDialog progressDialog = null;

	public void sendVideo(View view) {
		if (TextUtils.isEmpty(localPath)) {
			EMLog.e("Recorder", "recorder fail please try again!");
			return;
		}
		if(msc == null)
			msc = new MediaScannerConnection(this,
					new MediaScannerConnectionClient() {

						@Override
						public void onScanCompleted(String path, Uri uri) {
							EMLog.d(TAG, "scanner completed");
							msc.disconnect();
							progressDialog.dismiss();
							setResult(RESULT_OK, getIntent().putExtra("uri", uri));
							if(uri == null) {
								setResult(RESULT_OK, getIntent().putExtra("path", path));
							} else {
								setResult(RESULT_OK, getIntent().putExtra("uri", uri));
							}
							finish();
						}

						@Override
						public void onMediaScannerConnected() {
							msc.scanFile(localPath, "video/*");
						}
					});


		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getString(R.string.recorder_video_processing));
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
		msc.connect();

	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		EMLog.v("video", "onInfo");
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			EMLog.v("video", "max duration reached");
			stopRecording();
			btn_switch.setVisibility(View.VISIBLE);
			chronometer.stop();
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);
			chronometer.stop();
			if (localPath == null) {
				return;
			}
			String st3 = getResources().getString(R.string.Whether_to_send);
			new AlertDialog.Builder(this)
					.setMessage(st3)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
								                    int arg1) {
									arg0.dismiss();
									sendVideo(null);

								}
							}).setNegativeButton(R.string.cancel, null)
					.setCancelable(false).show();
		}

	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		EMLog.e("video", "recording onError:");
		stopRecording();
		ToastHelper.show(this, R.string.video_record_error);

	}

	public void saveBitmapFile(Bitmap bitmap) {
		File file = new File(Environment.getExternalStorageDirectory(), "a.jpg");
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}

	@Override
	public void onBackPressed() {
		back(null);
	}

	private void showFailDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.prompt)
				.setMessage(R.string.Open_the_equipment_failure)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
							                    int which) {
								finish();

							}
						}).setCancelable(false).show();

	}

	private void showNoSDCardDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.prompt)
				.setMessage("No sd card!")
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
							                    int which) {
								finish();

							}
						}).setCancelable(false).show();
	}

}

