package com.easemob.kefu_remote.shell;

import android.view.KeyEvent;

import com.easemob.kefu_remote.shell.exceptions.RootDeniedException;
import com.easemob.kefu_remote.shell.execution.Command;
import com.easemob.kefu_remote.utils.VMLog;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * root 命令工具类
 */
public class VMRShell {

    /**
     * keyevent 对应的值
     */

    /**
     * sendevent 相关事件类型
     */
    private static final int EV_SYN = 0;
    private static final int EV_KEY = 1;
    private static final int EV_ABS = 3;

    /**
     * sendevent 事件动作 按下 抬起
     */
    private static final int DOWN = 1;
    private static final int UP = 0;

    /**
     * sendevent 事件 key
     */
    private static final int BTN_TOUCH = 330;
    private static final int BTN_TOOL_FINGER = 325;

    private static final int KEY_VOLUMEDOWN = 114;
    private static final int KEY_VOLUMEUP = 115;
    private static final int KEY_POWER = 116;
    private static final int KEY_BACK = 102;
    private static final int KEY_HOME = 102;
    private static final int KEY_MENU = 103;

    private static final int ABS_MT_TRACKING_ID = 57;
    private static final int ABS_MT_POSITION_X = 53;
    private static final int ABS_MT_POSITION_Y = 54;
    private static final int ABS_MT_TOUCH_MAJOR = 48;

    private static final int SYN_MT_REPORT = 2;
    private static final int SYN_REPORT = 0;

    private static final int KEY_1 = 10;

    public static void execCommand(String cmd) {
        Command command = new Command(0, cmd);
        try {
            RootShell.getShell(true).add(command);
        } catch (IOException e) {
            VMLog.e("命令执行失败 %s", e.getMessage());
            e.printStackTrace();
        } catch (TimeoutException e) {
            VMLog.e("命令执行失败 %s", e.getMessage());
            e.printStackTrace();
        } catch (RootDeniedException e) {
            VMLog.e("命令执行失败 %s", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 模拟音量+
     */
    public static void volumeUp() {
        execCommand(formatSendEvent("event1", EV_KEY, KEY_VOLUMEUP, DOWN));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
        execCommand(formatSendEvent("event1", EV_KEY, KEY_VOLUMEUP, UP));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟音量-
     */
    public static void volumeDown() {
        execCommand(formatSendEvent("event0", EV_KEY, KEY_VOLUMEDOWN, DOWN));
        execCommand(formatSendEvent("event0", EV_SYN, SYN_REPORT, 0));
        execCommand(formatSendEvent("event0", EV_KEY, KEY_VOLUMEDOWN, UP));
        execCommand(formatSendEvent("event0", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟电源键
     */
    public static void powerKey() {
        execCommand(formatSendEvent("event0", EV_KEY, KEY_POWER, DOWN));
        execCommand(formatSendEvent("event0", EV_SYN, SYN_REPORT, 0));
        execCommand(formatSendEvent("event0", EV_KEY, KEY_POWER, UP));
        execCommand(formatSendEvent("event0", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟返回键按下
     */
    public static void keyBackDown() {
        execCommand(formatSendEvent("event1", EV_KEY, KEY_BACK, DOWN));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟返回键抬起
     */
    public static void keyBackUp() {
        execCommand(formatSendEvent("event1", EV_KEY, KEY_BACK, UP));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * Home 键
     */
    public static void keyHome() {
        execCommand(formatSendEvent("event1", EV_KEY, KEY_HOME, DOWN));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
        execCommand(formatSendEvent("event1", EV_KEY, KEY_HOME, UP));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 菜单键
     */
    public static void keyMenu() {
        execCommand(formatSendEvent("event1", EV_KEY, KEY_MENU, DOWN));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
        execCommand(formatSendEvent("event1", EV_KEY, KEY_MENU, UP));
        execCommand(formatSendEvent("event1", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟触摸按下操作
     *
     * @param x 触摸 x 坐标
     * @param y 触摸 y 坐标
     * @param index 事件序号
     */
    public static void touchDown(int x, int y, int index) {
        // 小米4
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_TRACKING_ID, index));
        //        execCommand(formatSendEvent("event2", EV_KEY, BTN_TOUCH, DOWN));
        //        execCommand(formatSendEvent("event2", EV_KEY, BTN_TOOL_FINGER, DOWN));
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_POSITION_X, x));
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_POSITION_Y, y));
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_TOUCH_MAJOR, 8));
        //        execCommand(formatSendEvent("event2", EV_SYN, SYN_REPORT, 0));
        // 华为荣耀3C
        //        execCommand(formatSendEvent("event3", EV_ABS, ABS_MT_TRACKING_ID, index));
        //        execCommand(formatSendEvent("event3", EV_KEY, BTN_TOUCH, DOWN));
        //        execCommand(formatSendEvent("event3", EV_ABS, ABS_MT_POSITION_X, x));
        //        execCommand(formatSendEvent("event3", EV_ABS, ABS_MT_POSITION_Y, y));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_MT_REPORT, 0));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_REPORT, 0));

        // 科大 阿尔法.蛋
        execCommand(formatSendEvent("event4", EV_ABS, ABS_MT_TRACKING_ID, index));
        execCommand(formatSendEvent("event4", EV_KEY, BTN_TOUCH, DOWN));
        execCommand(formatSendEvent("event4", EV_ABS, ABS_MT_POSITION_X, x));
        execCommand(formatSendEvent("event4", EV_ABS, ABS_MT_POSITION_Y, y));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_MT_REPORT, 0));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟触摸移动操作
     *
     * @param x 触摸 x 坐标
     * @param y 触摸 y 坐标
     */
    public static void touchMove(int x, int y) {
        // 小米4
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_POSITION_X, x));
        //        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_POSITION_Y, y));
        //        execCommand(formatSendEvent("event2", EV_SYN, SYN_REPORT, 0));

        // 华为荣耀3C
        //        execCommand(formatSendEvent("event3", EV_ABS, ABS_MT_POSITION_X, x));
        //        execCommand(formatSendEvent("event3", EV_ABS, ABS_MT_POSITION_Y, y));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_MT_REPORT, 0));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_REPORT, 0));

        // 科大 阿尔法.蛋
        execCommand(formatSendEvent("event4", EV_ABS, ABS_MT_POSITION_X, x));
        execCommand(formatSendEvent("event4", EV_ABS, ABS_MT_POSITION_Y, y));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_MT_REPORT, 0));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 模拟触摸抬起操作
     */
    public static void touchUp() {
        // 小米4
        execCommand(formatSendEvent("event2", EV_ABS, ABS_MT_TRACKING_ID, -1));
        execCommand(formatSendEvent("event2", EV_KEY, BTN_TOUCH, UP));
        execCommand(formatSendEvent("event2", EV_KEY, BTN_TOOL_FINGER, UP));
        execCommand(formatSendEvent("event2", EV_SYN, SYN_REPORT, 0));

        // 华为荣耀3C
        //        execCommand(formatSendEvent("event3", EV_KEY, BTN_TOUCH, UP));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_MT_REPORT, 0));
        //        execCommand(formatSendEvent("event3", EV_SYN, SYN_REPORT, 0));

        // 科大 阿尔法.蛋
        execCommand(formatSendEvent("event4", EV_KEY, BTN_TOUCH, UP));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_MT_REPORT, 0));
        execCommand(formatSendEvent("event4", EV_SYN, SYN_REPORT, 0));
    }

    /**
     * 格式化 sendevent 类型的命令
     *
     * @param eventName 传感器对应事件名称
     * @param ev 事件类型
     * @param key 事件操作 key
     * @param value 事件值
     * @return
     */
    public static String formatSendEvent(String eventName, int ev, int key, int value) {
        return String.format("sendevent /dev/input/%s %d %d %d", eventName, ev, key, value);
    }


    /**
     * --------------------- 模拟 Input 事件 ---------------------
     */

    /**
     * 模拟屏幕点击
     *
     * @param x 点击位置 x 坐标
     * @param y 点击位置 y 坐标
     */
    public static void inputTap(int x, int y) {
        execCommand(String.format("input tap %d %d \n", x, y));
    }

    /**
     * 模拟屏幕滑动
     *
     * @param startX 滑动开始 x 坐标
     * @param startY 滑动开始 y 坐标
     * @param endX 滑动结束 x 坐标
     * @param endY 滑动结束 y 坐标
     */
    public static void inputSwipe(int startX, int startY, int endX, int endY) {
        execCommand(String.format("input swipe %d %d %d %d\n", startX, startY, endX, endY));
    }

    public static void scrollDown(int x, int y) {
        inputSwipe(x, y, x, y + 200);
    }

    public static void scrollUp(int x, int y) {
        inputSwipe(x, y, x, y - 200);
    }

    /**
     * 模拟输入文本
     *
     * @param text 需要输入的文本内容
     */
    public static void inputText(String text) {
        execCommand(String.format("input text '%s' \n", text));
    }

    /**
     * 模拟按键
     *
     * @param keyCode 按键值
     */
    public static void inputKeyEvent(int keyCode) {
        execCommand(String.format("input keyevent %d \n", keyCode));
    }

    /**
     * 模拟返回键
     */
    public static void back() {
        inputKeyEvent(KeyEvent.KEYCODE_BACK);
    }

    /**
     * 模拟 keyCode 对应的按键事件
     *
     * @param code 指定 keycode
     */
    public static void keyCode(int code) {
        inputKeyEvent(wrapKeyCode(code));
    }

    /**
     * 包装远程发来的按键操作，这些主要是键盘按键，此方法主要是将远程发来的按键代码转为 android 上对应的 key 值
     *
     * @param code 远程按键
     */
    private static int wrapKeyCode(int code) {
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
