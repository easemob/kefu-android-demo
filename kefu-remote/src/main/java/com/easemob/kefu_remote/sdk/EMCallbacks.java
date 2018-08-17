package com.easemob.kefu_remote.sdk;

/**
 * Created by lzan13 on 2017/11/22.
 */

public interface EMCallbacks {
    void onDone(Object object);

    void onError(int code, String errorDesc);
}
