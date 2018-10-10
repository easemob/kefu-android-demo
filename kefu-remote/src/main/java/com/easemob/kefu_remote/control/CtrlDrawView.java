package com.easemob.kefu_remote.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lzan13 on 2018/5/14.
 */
public class CtrlDrawView extends View {

    private Context context;
    // 画笔
    protected Paint paint;
    protected int lineColor = 0xffff8989;
    protected int lineWidth = 8;
    protected Path path;
    protected float startX, startY, stopX, stopY;

    protected boolean isDown = false;

    public CtrlDrawView(Context context) {
        this(context, null);
    }

    public CtrlDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtrlDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        path = new Path();
        // 实例化画笔
        paint = new Paint();
        // 设置画笔颜色
        paint.setColor(lineColor);
        // 设置抗锯齿
        paint.setAntiAlias(true);
        // 效果同上
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // 设置画笔宽度
        paint.setStrokeWidth(lineWidth);
        // 设置画笔模式
        paint.setStyle(Paint.Style.STROKE);
        // 设置画笔末尾样式
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPath(canvas);
        drawDown(canvas);
    }

    private void drawDown(Canvas canvas) {
        if (isDown) {
            canvas.drawCircle(startX, startY, 15, paint);
        }
    }

    private void drawPath(Canvas canvas) {
        canvas.drawPath(path, paint);
        //canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touchDown(event);
            break;
        case MotionEvent.ACTION_MOVE:
            touchMove(event);
            break;
        case MotionEvent.ACTION_UP:
            touchUp(event);
            break;
        }
        invalidate();
        return true;
    }

    private void touchDown(MotionEvent event) {
        isDown = true;
        startX = event.getX();
        startY = event.getY();
        path.moveTo(startX, startY);
    }

    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final float previousX = startX;
        final float previousY = startY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            path.quadTo(previousX, previousY, cX, cY);
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            startX = x;
            startY = y;
        }
    }

    private void touchUp(MotionEvent event) {
        //path.reset();
    }

    /**
     * 清空画布
     */
    public void clear() {
        path.reset();
        postInvalidate();
    }
}
