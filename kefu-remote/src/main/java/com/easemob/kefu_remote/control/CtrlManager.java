package com.easemob.kefu_remote.control;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.easemob.kefu_remote.RemoteApp;
import com.easemob.kefu_remote.conference.RemoteManager;
import com.easemob.kefu_remote.sdk.EMConferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 远程控制管理类
 */
public class CtrlManager {

    // 服务器发来的事件类型
    public static final int EVENT_ACTION = 0;   // 动作
    public static final int EVENT_CTRL_REQUEST = 1; // 申请控制
    public static final int EVENT_CTRL_END = 2;     // 控制结束
    public static final int EVENT_CTRL_PING = 3;    // ping

    /**
     * oper:表示鼠标动作 UP: 0, DOWN: 1, MOVE: 2
     * btn:表示鼠标事件 LEFT: 1, WHEEL: 2, RIGHT: 4, WHEEL_ROLL_UP: 8, WHEEL_ROLL_DOWN: 16,
     */
    private final int ACTION_MOUSE_UP = 0;
    private final int ACTION_MOUSE_DOWN = 1;
    private final int ACTION_MOUSE_MOVE = 2;
    private final int ACTION_KEY_DOWN = 3;
    private final int ACTION_KEY_UP = 4;

    private final int MOUSE_LEFT = 1;
    private final int MOUSE_RIGHT = 4;
    private final int MOUSE_SCROLL_DOWN = 8;
    private final int MOUSE_SCROLL_UP = 16;

    private int CODE_RESPONSE = 128;
    private int CODE_REJECT = -402;
    private int CODE_BUSY = -403;
    private int CODE_FAIL = -404;

    // 被控制
    private boolean isCtrl = false;
    // 按下
    private boolean isDown = false;

    private String remoteMemberId = "";
    private String streamId = "";
    private String ctrlId = "";
    private Object objectId = null;
    private int preX, preY;

    private static CtrlManager instance;
    private Context context;
    // 窗口管理器
    private WindowManager windowManager;
    // 鼠标指针
    private CtrlMouseWidget mouseWidget;
    private WindowManager.LayoutParams cursorParams;
    // 屏幕宽高，用来转换坐标
    private int screenWidth;
    private int screenHeight;

    // 模拟控制线程
    private CtrlMotion ctrlMotion;

    private CtrlManager() {
        context = RemoteApp.getInstance().getContext();
        initWindowManager();
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        ctrlMotion = new CtrlMotion();
    }

    public static CtrlManager getInstance() {
        if (instance == null) {
            instance = new CtrlManager();
        }
        return instance;
    }

    /**
     * 开启控制模式
     */
    private void startCtrlMode() {
        isCtrl = true;
        initWindowManager();
        if (mouseWidget == null) {
            mouseWidget = new CtrlMouseWidget(context);
            if (cursorParams == null) {
                cursorParams = new WindowManager.LayoutParams();
                // 设置窗口类型
                cursorParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                // 设置悬浮窗透明
                cursorParams.format = PixelFormat.TRANSPARENT;
                // 位置为左侧顶部
                cursorParams.gravity = Gravity.LEFT | Gravity.TOP;
                // 设置宽高自适应
                cursorParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                cursorParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                // 设置窗口标志类型，其中 FLAG_NOT_FOCUSABLE 是放置当前悬浮窗拦截点击事件，造成桌面控件不可操作
                cursorParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }
            mouseWidget.setParams(cursorParams);
            windowManager.addView(mouseWidget, cursorParams);
        }
    }

    /**
     * 停止控制模式
     */
    public void stopCtrlMode() {
        isCtrl = false;
        if (mouseWidget != null) {
            initWindowManager();
            windowManager.removeView(mouseWidget);
            mouseWidget = null;
        }
    }

    /**
     * ------------------------------ 控制相关消息处理 ------------------------------
     *
     * 解析收到的控制消息
     */
    public int parseCtrlMsg(String msg, String memberId, Object obj) {
        int event = -1;
        remoteMemberId = memberId;
        objectId = obj;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            streamId = jsonObject.optString("streamId");
            ctrlId = jsonObject.optString("cId");
            event = jsonObject.optInt("evt");
            if (event == EVENT_CTRL_REQUEST) {
                RemoteManager.getInstance().requestCtrDialog();
            } else if (event == EVENT_CTRL_END) {
                stopCtrlMode();
                listener.stopCtr();
            } else if (event == EVENT_CTRL_PING) {
                sendPingCtrl();
            } else if (event == EVENT_ACTION) {
                parseAction(jsonObject.optJSONArray("actions"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    private StopCtrModeListener listener;

    public void setStopCtrModeListener(StopCtrModeListener listener) {
        this.listener = listener;
    }

    public interface StopCtrModeListener {
        void stopCtr();
    }

    /**
     * 解析 Action 类型的消息
     */
    private void parseAction(JSONArray jsonArray) throws JSONException {
        int sn = 0;
        CtrlAction action;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            sn = jsonObject.optInt("sn");
            int oper = jsonObject.optInt("oper");
            int btn = jsonObject.optInt("btn");
            int x = jsonObject.optInt("x");
            int y = jsonObject.optInt("y");

            action = new CtrlAction(oper, x, y);
            action.setBtn(btn);
            action.setIndex(sn);
            execAction(action);
        }

        JSONObject resObject = new JSONObject();
        resObject.put("sn", sn);
        sendCtrlMsg(remoteMemberId, CODE_RESPONSE, objectId, resObject.toString());
    }

    /**
     * 回复 Ping 消息
     */
    private void sendPingCtrl() {
        JSONObject jsonObject = new JSONObject();
        sendCtrlMsg(remoteMemberId, CODE_RESPONSE, objectId, jsonObject.toString());
    }

    /**
     * 同意申请控制
     */
    public void agreeRequestCtrl() {
        startCtrlMode();
        sendCtrlMsg(remoteMemberId, CODE_RESPONSE, objectId, "");
    }

    /**
     * 拒绝申请控制
     */
    public void rejectRequestCtrl() {
        sendCtrlMsg(remoteMemberId, CODE_REJECT, objectId, "");
    }

    /**
     * 回复控制消息
     */
    private void sendCtrlMsg(String memberId, int code, Object objectId, String msg) {
        EMConferenceManager.getInstance().sendCtrlMsgByMemberId(memberId, code, objectId, msg);
    }

    /**
     * 执行事件
     */
    private void execAction(CtrlAction action) {
        if (!isCtrl) {
            return;
        }
        if (mouseWidget == null) {
            return;
        }
        // 事件触发坐标
        int evX = relativePositionX(action.getX());
        int evY = relativePositionY(action.getY());
        // UI 显示坐标
        int x = evX;
        int y = evY;
        switch (action.getOper()) {
            case ACTION_MOUSE_DOWN: // 鼠标按下
                if (action.getBtn() == MOUSE_LEFT) {
                    ctrlMotion.touchDown(x, y);
                    isDown = true;
                    mouseWidget.mouseDown();
                } else if (action.getBtn() == MOUSE_RIGHT) {
                    ctrlMotion.back();
                } else if (action.getBtn() == MOUSE_SCROLL_DOWN) {
                    //                VMRShell.scrollDown(x, y);
                } else if (action.getBtn() == MOUSE_SCROLL_UP) {
                    //                VMRShell.scrollUp(x, y);
                }
                break;
            case ACTION_MOUSE_MOVE: // 鼠标移动
                mouseWidget.updatePosition(x, y);
                if (isDown) {
                    ctrlMotion.touchMove(x, y);
                }
                break;
            case ACTION_MOUSE_UP:   // 鼠标抬起
                isDown = false;
                mouseWidget.mouseUp();
                ctrlMotion.touchUp(x, y);
                break;
            case ACTION_KEY_DOWN:   // 按键按下
                break;
            case ACTION_KEY_UP:     // 按键抬起
                ctrlMotion.keyCode(action.getBtn());
                break;
        }
        preX = evX;
        preY = evY;
    }

    /**
     * 转换 x 坐标
     */
    private int relativePositionX(int x) {
        return (int) ((x / 10000.0f) * screenWidth);
    }

    /**
     * 转换 y 坐标
     */
    private int relativePositionY(int y) {
        return (int) ((y / 10000.0f) * screenHeight);
    }

    /**
     * 获取窗口管理器
     */
    private void initWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    public void reset() {
        stopCtrlMode();
        remoteMemberId = "";
        streamId = "";
        ctrlId = "";
        objectId = null;
    }
}
