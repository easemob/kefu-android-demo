package com.hyphenate.helpdesk;


public class Error {

    /**
     * 无错误
     */
    public final static int NO_ERROR = 0;
    /**
     * 一般错误，未细分的错误一般通过此errorcode抛出来
     */
    public final static int GENERAL_ERROR = 1;
    /**
     * 网络异常
     */
    public final static int NETWORK_ERROR = 2;
    /**
     * appkey不正确
     */
    public final static int INVALID_APP_KEY = 100;
    /**
     * 用户id不正确
     */
    public final static int INVALID_USER_NAME = 101;
    /**
     * 密码不正确
     */
    public final static int INVALID_PASSWORD = 102;

    /**
     * 用户已登录
     */
    public final static int USER_ALREADY_LOGIN = 200;
    /**
     * 用户未登录
     */
    public final static int USER_NOT_LOGIN = 201;
    /**
     * 用户id或密码错误
     */
    public final static int USER_AUTHENTICATION_FAILED = 202;
    /**
     * 用户已经存在
     */
    public final static int USER_ALREADY_EXIST = 203;
    /**
     * 不存在此用户
     */
    public final static int USER_NOT_FOUND = 204;
    /**
     * 参数不合法
     */
    public final static int USER_ILLEGAL_ARGUMENT = 205;
    /**
     * 账户在另外一台设备登录
     */
    public final static int USER_LOGIN_ANOTHER_DEVICE = 206;
    /**
     * 账户被删除
     */
    public final static int USER_REMOVED = 207;
    /**
     * 注册失败
     */
    public final static int USER_REG_FAILED = 208;
    /**
     * 更新用户信息失败
     */
    public final static int USER_UPDATEINFO_FAILED = 209;
    /**
     * 用户没有该操作权限
     */
    public final static int USER_PERMISSION_DENIED = 210;
    /**
     * 绑定设备token失败
     */
    public final static int USER_BINDDEVICETOKEN_FAILED = 211;
    /**
     * 解绑设备token失败
     */
    public final static int USER_UNBIND_DEVICETOKEN_FAILED = 212;
    /**
     * 无法访问到服务器
     */
    public final static int SERVER_NOT_REACHABLE = 300;
    /**
     * 等待服务器响应超时
     */
    public final static int SERVER_TIMEOUT = 301;
    /**
     * 服务器繁忙
     */
    public final static int SERVER_BUSY = 302;
    /**
     * 未知的server异常
     */
    public final static int SERVER_UNKNOWN_ERROR = 303;

    /**
     * 文件不存在
     */
    public final static int FILE_NOT_FOUND = 400;

    /**
     * 文件不合法
     */
    public final static int FILE_INVALID = 401;

    /**
     * 文件上传失败
     */
    public final static int FILE_UPLOAD_FAILED = 402;

    /**
     * 文件下载失败
     */
    public final static int FILE_DOWNLOAD_FAILED = 403;

    /**
     * 消息不合法
     */
    public final static int MESSAGE_INVALID = 500;

    /**
     * 消息内容包含非法或敏感词
     */
    public final static int MESSAGE_INCLUDE_ILLEGAL_CONTENT = 501;

    /**
     * 消息发送过快，触发限流
     *
     */
    public final static int MESSAGE_SEND_TRAFFIC_LIMIT = 502;

}