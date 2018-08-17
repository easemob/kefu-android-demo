package com.easemob.kefu_remote.control;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class CtrlMotion {

    private Instrumentation instrumentation;
    private HandlerThread handlerThread;
    private Handler handler;

    public CtrlMotion() {
        instrumentation = new Instrumentation();
        handlerThread = new HandlerThread("CtrlMotion");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    /**
     * 触摸屏幕按下事件
     *
     * @param x
     * @param y
     */
    public void touchDown(final int x, final int y) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
                instrumentation.sendPointerSync(event);
            }
        });
    }

    /**
     * 触摸屏幕移动事件
     *
     * @param x
     * @param y
     */
    public void touchMove(final int x, final int y) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y, 0);
                instrumentation.sendPointerSync(event);
            }
        });
    }

    /**
     * 触摸屏幕抬起事件
     *
     * @param x
     * @param y
     */
    public void touchUp(final int x, final int y) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
                instrumentation.sendPointerSync(event);
            }
        });
    }

    /**
     * 返回
     */
    public void back() {
        sendKeyCode(KeyEvent.KEYCODE_BACK);
    }

    /**
     * 回到主界面
     */
    public void home() {
        sendKeyCode(KeyEvent.KEYCODE_HOME);
    }

    private void sendKeyCode(final int key) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                instrumentation.sendCharacterSync(key);
            }
        });
    }

    /**
     * 模拟 keyCode 对应的按键事件
     *
     * @param code 指定 keycode
     */
    public void keyCode(int code) {
        sendKeyCode(wrapKeyCode(code));
    }

    /**
     * 包装远程发来的按键操作，这些主要是键盘按键，此方法主要是将远程发来的按键代码转为 android 上对应的 key 值
     *
     * @param code 远程按键
     */
    private int wrapKeyCode(int code) {
        int keyCode = KeyEvent.KEYCODE_UNKNOWN;
        switch (code) {
        case 8:
            keyCode = KeyEvent.KEYCODE_DEL;
            break;
        case 9:
            keyCode = KeyEvent.KEYCODE_TAB;
            break;
        case 13:
            keyCode = KeyEvent.KEYCODE_ENTER;
            break;
        case 16:
            keyCode = KeyEvent.KEYCODE_SHIFT_LEFT;
            break;
        case 27:
            keyCode = KeyEvent.KEYCODE_BACK;
            break;
        case 32:
            keyCode = KeyEvent.KEYCODE_SPACE;
            break;
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
            // 这里是将远程发来的 0-9 转为 android 上的 0-9
            keyCode = code - 41;
            break;
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
            // 这里是将远程发来的 a-z 转为 android 上的 a-z
            keyCode = code - 36;
            break;
        }
        return keyCode;
    }
}
