package com.easemob.helpdeskdemo.utils;

import okhttp3.Request;

/**
 */
public final class Progress {

    public void run() throws Exception{
        Request request = new Request.Builder().url("").build();




    }


    interface ProgressListener{
        void update(long bytesRead, long contentLength, boolean done);
    }

}
