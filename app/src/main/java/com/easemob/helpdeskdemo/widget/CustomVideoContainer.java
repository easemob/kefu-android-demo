package com.easemob.helpdeskdemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liyuzhao on 21/07/2017.
 */

public class CustomVideoContainer extends ViewGroup {

	private boolean isShowMax;
	private CustomVideoView maxView;
	private OnMaxVideoChangeListener changeListener;

	public CustomVideoContainer(Context context) {
		super(context);
		init();
	}

	public CustomVideoContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		setBackgroundColor(Color.parseColor("#232323"));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!isShowMax){
			int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
			setMeasuredDimension(sizeWidth, sizeWidth);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = getWidth();
		int videoWidth = width / 2;
		int cCount = getChildCount();
		if (isShowMax && maxView != null) {
			for (int i = 0; i < cCount; i++) {
				View childView = getChildAt(i);
				if (childView != maxView) {
					childView.layout(0, 0, 1, 1);
				} else {
					childView.layout(0, 0, getWidth(), getHeight());
				}
			}
		} else {
			for (int i = 0; i < cCount; i++) {
				View childView = getChildAt(i);
				int cl = videoWidth * (i % 2);
				int ct = videoWidth * (i / 2);
				int cr = videoWidth * (i % 2 + 1);
				int cb = videoWidth * (i / 2 + 1);
				childView.layout(cl, ct, cr, cb);
			}
		}
	}

	@Override
	public void addView(View child) {
		if (child instanceof CustomVideoView){
			final CustomVideoView childVideoView = (CustomVideoView) child;
			childVideoView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isShowMax){
						isShowMax = true;
						maxView = childVideoView;
						maxView.setLargeScreen(true);
						if (changeListener != null){
							changeListener.onChanged(isShowMax);
						}
						requestLayout();
						postInvalidate();
					}


				}
			});
		}
		super.addView(child);
	}


	public synchronized void removeVideoView(CustomVideoView videoView) {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof CustomVideoView) {
				CustomVideoView item = (CustomVideoView) view;
				if (item == videoView) {
					item.release();
					if (maxView != null && maxView == view) {
						isShowMax = false;
						maxView = null;
						if (changeListener != null){
							changeListener.onChanged(false);
						}
					}
					removeViewAt(i);
					break;
				}
			}
		}
	}

	public synchronized void removeAllVideoViews() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof CustomVideoView) {
				CustomVideoView item = (CustomVideoView) view;
				item.release();
//				removeView(view);
			}
		}
		removeAllViews();
		isShowMax = false;
		maxView = null;
		if (changeListener != null){
			changeListener.onChanged(false);
		}
	}

	public void minimizeChildView(){
		isShowMax = false;
		maxView.setLargeScreen(false);
		requestLayout();
		postInvalidate();
	}


	public void setOnMaxVideoChangeListener(OnMaxVideoChangeListener listener){
		this.changeListener = listener;
	}

	public static interface OnMaxVideoChangeListener {
		void onChanged(boolean isMax);
	}

}
