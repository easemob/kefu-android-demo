package com.easemob.kefu_remote.utils;

import android.util.Log;

/**
 * Created by lzan13 on 2014/12/16.
 * log日志输出封装类
 */
public class VMLog {
    /**
     * %s   字符串类型      "mingrisoft"
     * %c   字符类型        'm'
     * %b   布尔类型        true
     * %d   整数类型（十进制    99
     * %x   整数类型（十六进制）FF
     * %o   整数类型（八进制）
     * 77
     * %f   浮点类型    99.99
     * %a   十六进制浮点类型    FF.35AE
     * %e   指数类型  9.38e+5
     * %g   通用浮点类型（f和e类型中较短的）
     * %h   散列码
     * %%   百分比类型
     * ％
     * %n   换行符
     * %tx  日期与时间类型（x代表不同的日期与时间转换符
     */

    // 这里设置默认的 Tag
    private static String mTag = "VMTools";
    private static final int LEVEL_NORMAL = 0;
    private static final int LEVEL_INFO = 1;
    private static final int LEVEL_DEBUG = 2;
    private static final int LEVEL_ERROR = 3;
    private static final int LEVEL_NONE = 4;

    private static int mLevel = LEVEL_NORMAL;

    /**
     * 初始化日志 Tag，即设置自己的TAG
     */
    public static void initTag(String tag) {
        mTag = tag;
    }

    /**
     * 设置 Debug 输出级别
     *
     * @param level debug 级别
     */
    public static void setDebug(int level) {
        mLevel = level;
    }

    /**
     * 根据堆栈信息定位 Log
     * 生成 Log 为 类名 + 方法名 + 行数
     *
     * @return 返回 Log
     */
    private static String generateLog() {
        StackTraceElement element = getCallerStackTraceElement();

        String log = "%s.%s (%s:%d)";
        // 获取堆栈信息中调用当前方法的类名
        String className = element.getClassName();
        // 截取简单类名
        className = className.substring(className.lastIndexOf(".") + 1);
        // 格式化 log 内容
        log = String.format(log, className, element.getMethodName(), element.getFileName(),
            element.getLineNumber());
        return log;
    }

    /**
     * StackTrace用栈的形式保存了方法的调用信息；
     * 可用 Thread.currentThread().getStackTrace()方法得到当前线程的 StackTrace 信息；
     * 该方法返回的是一个 StackTraceElement 数组；
     * <p/>
     * 在 StackTraceElement 数组下标为[2]的元素中保存了当前方法的所属文件名，当前方法所属的类名,
     * 以及该方法的名字；除此以外还可以获取方法调用的行数；
     * 在 StackTraceElement 数组下标为[3]的元素中保存了当前方法的调用者的信息和它调用时的代码行数；
     */
    private static StackTraceElement getCallerStackTraceElement() {
        // 所以这里选择第三个元素，用来获取调用当前方法的类和方法名以及行数
        return Thread.currentThread().getStackTrace()[6];
    }

    /**
     * 获取线程信息
     */
    private static String getThreadInfo() {
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        return threadName + " - " + threadId;
    }

    /**
     * 获取类文件全名称
     */
    private static String getClassFileName() {
        return getCallerStackTraceElement().getFileName();
    }

    /**
     * 输出 Info 日志信息
     *
     * @param message 日志内容
     */
    public static void i(String message) {
        if (mLevel <= LEVEL_INFO) {
            print(Level.INFO, message);
        }
    }

    /**
     * 输出 Debug 日志信息
     *
     * @param message 日志内容
     */
    public static void d(String message) {
        if (mLevel <= LEVEL_DEBUG) {
            print(Level.DEBUG, message);
        }
    }

    /**
     * 输出 Error 日志信息
     *
     * @param message 日志内容
     */
    public static void e(String message) {
        if (mLevel <= LEVEL_ERROR) {
            print(Level.ERROR, message);
        }
    }

    /**
     * 使用格式化的方式输出 Info 日志信息
     *
     * @param msg 需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void i(String msg, Object... args) {
        if (mLevel <= LEVEL_INFO) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            print(Level.INFO, message);
        }
    }

    /**
     * 使用格式化的方式输出 Debug 日志信息
     *
     * @param msg 需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void d(String msg, Object... args) {
        if (mLevel <= LEVEL_DEBUG) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            print(Level.DEBUG, message);
        }
    }

    /**
     * 使用格式化的方式输出 Error 日志信息
     *
     * @param msg 需要格式化的样式
     * @param args 要格式化的信息
     */
    public static void e(String msg, Object... args) {
        if (mLevel <= LEVEL_ERROR) {
            String message = args.length == 0 ? msg : String.format(msg, args);
            print(Level.ERROR, message);
        }
    }

    /**
     * 输出日志
     *
     * @param level 日志级别
     * @param message 日志内容
     */
    private static void print(Level level, String message) {
        switch (level) {
        case DEBUG:
            Log.d(mTag, " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.d(mTag, " ┃ Thread:" + getThreadInfo());
            Log.d(mTag, " ┃ " + generateLog());
            Log.d(mTag, " ┃ " + message);
            Log.d(mTag, " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            break;
        case INFO:
            Log.i(mTag, " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.i(mTag, " ┃ Thread:" + getThreadInfo());
            Log.i(mTag, " ┃ " + generateLog());
            Log.i(mTag, " ┃ " + message);
            Log.i(mTag, " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            break;
        case ERROR:
            Log.e(mTag, " ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.e(mTag, " ┃ Thread:" + getThreadInfo());
            Log.e(mTag, " ┃ " + generateLog());
            Log.e(mTag, " ┃ " + message);
            Log.e(mTag, " ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            break;
        }
    }

    public enum Level {
        NORMAL, INFO, DEBUG, ERROR, NONE;
    }
}