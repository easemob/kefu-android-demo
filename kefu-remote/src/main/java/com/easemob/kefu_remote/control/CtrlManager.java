package com.easemob.kefu_remote.control;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.easemob.kefu_remote.RemoteManager;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;
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

    // 被控制
    private boolean isCtrl = false;
    // 按下
    private boolean isDown = false;

    private String remoteMemberId = "";
    private Object objectId = null;
    private String streamId = "";
    private String ctrlId = "";
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

    // To calculate the status bar height
    private View anchor;
    private WindowManager.LayoutParams anchorParams;
    private int statusBarHeight = 0;

    // 模拟控制线程
    private CtrlMotion ctrlMotion;

    private CtrlManager(Context context) {
        this.context = context;
        initWindowManager();

        Point point = new Point();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            windowManager.getDefaultDisplay().getRealSize(point);
        } else {
            windowManager.getDefaultDisplay().getSize(point);
        }
        screenWidth = point.x;
        screenHeight = point.y;

        ctrlMotion = new CtrlMotion();
    }

    public static CtrlManager getInstance(Context context) {
        if (instance == null) {
            instance = new CtrlManager(context);
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

        // 添加一个单位像素的view到app的左上角，根据该view在手机整个屏幕中的位置来判断状态栏是否显示和状态栏的高度。
        if (anchorParams == null) {
            anchorParams = new WindowManager.LayoutParams();
            anchorParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            anchorParams.format = PixelFormat.TRANSPARENT;
            anchorParams.gravity = Gravity.LEFT | Gravity.TOP;
            anchorParams.width = 1;
            anchorParams.height = 1;
            anchorParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        }

        if (anchor == null) {
            anchor = new View(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1, 1);
            anchor.setLayoutParams(params);
        }

        windowManager.addView(anchor, anchorParams);
        anchor.post(new Runnable() {
            @Override public void run() {
                int[] params = new int[2];
                anchor.getLocationOnScreen(params);
                statusBarHeight = params[1];
                Log.i("CtrlManager", "statusBarHeight: " + statusBarHeight);
                windowManager.removeView(anchor);
            }
        });
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

    // 绘制面板
    private CtrlDrawWidget drawWidget;
    private WindowManager.LayoutParams drawParams;

    /**
     * 开始绘制模式
     */
    private void startDrawMode() {
        if (!isCtrl) {
            return;
        }
        initWindowManager();
        if (drawWidget == null) {
            drawWidget = new CtrlDrawWidget(context);
            if (drawParams == null) {
                drawParams = new WindowManager.LayoutParams();
                // 设置窗口类型
                drawParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                // 设置悬浮窗透明
                drawParams.format = PixelFormat.TRANSPARENT;
                // 位置为左侧顶部
                drawParams.gravity = Gravity.LEFT | Gravity.TOP;
                // 设置宽高自适应
                drawParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                drawParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                // 设置窗口标志类型，其中 FLAG_NOT_FOCUSABLE 是放置当前悬浮窗拦截点击事件，造成桌面控件不可操作
                drawParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            }
            windowManager.addView(drawWidget, drawParams);
        }
    }

    /**
     * 停止绘制模式
     */
    public void stopDrawMode() {
        if (drawWidget != null) {
            initWindowManager();
            windowManager.removeView(drawWidget);
            drawWidget = null;
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
//                 stopDrawMode();
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
        sendCtrlMsg(remoteMemberId, objectId, resObject.toString(), true);
    }

    /**
     * 回复 Ping 消息
     */
    private void sendPingCtrl() {
        JSONObject jsonObject = new JSONObject();
        sendCtrlMsg(remoteMemberId, objectId, jsonObject.toString(), true);
    }

    /**
     * 同意申请控制
     */
    public void agreeRequestCtrl() {
        startCtrlMode();
//         startDrawMode();
        sendCtrlMsg(remoteMemberId, objectId, "", true);
    }

    /**
     * 拒绝申请控制
     */
    public void rejectRequestCtrl() {
        sendCtrlMsg(remoteMemberId, objectId, "", false);
    }

    /**
     * 回复控制消息
     */
    private void sendCtrlMsg(String memberId, Object object, String msg, boolean isAgree) {
        ChatClient.getInstance().callManager().sendCustomWithRemoteMemberId(memberId, object, msg, isAgree, new Callback() {
            @Override public void onSuccess() {

            }

            @Override public void onError(int code, String error) {

            }

            @Override public void onProgress(int progress, String status) {

            }
        });
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
                mouseWidget.updatePosition(x, y - statusBarHeight);
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
