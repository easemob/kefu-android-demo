package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.CallSurfaceView;
import com.superrtc.sdk.VideoView;

/**
 * Created by liyuzhao on 25/07/2017.
 */

public class CustomVideoView extends ViewGroup {

	private TextView labelView;
	private CallSurfaceView surfaceView;
	private boolean isLargeScreen;

	public CustomVideoView(Context context) {
		this(context, null);
	}

	public CustomVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++){
			View childView = getChildAt(i);
			if (childView instanceof TextView){
				if (isLargeScreen){
					childView.layout(0, 0, 1, 1);
				}else{
					childView.layout(dip2px(2), getHeight() - dip2px(30), getWidth(), getHeight() - dip2px(2));
				}
			}else if (childView instanceof CallSurfaceView){
				childView.layout(0, 0, getWidth(), getHeight());
			}
		}
	}

	private void init(){
//		setBackgroundColor(Color.WHITE);

		surfaceView = new CallSurfaceView(getContext());
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		surfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
		addView(surfaceView);

		labelView = new TextView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		labelView.setLayoutParams(lp);
		labelView.setSingleLine(true);
		labelView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		labelView.setTextSize(16);
		labelView.setTextColor(Color.WHITE);
		addView(labelView);



	}

	public CallSurfaceView getSurfaceView(){
		return surfaceView;
	}

	public void setLargeScreen(boolean enable) {
		this.isLargeScreen = enable;
		if (enable) {
			surfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
		} else {
			surfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
		}
		invalidate();
	}

	public void setSurfaceViewVisible(boolean enable){
		surfaceView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}

	public void setLabel(CharSequence text){
		labelView.setText(text);
	}


	public int dip2px(float dpValue){
		final float scale =  getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}


	public void release(){
		if (surfaceView != null){
			surfaceView.release();
		}
	}

}
