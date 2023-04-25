package com.easemob.veckit;


import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetWork {
    public static int DEFAULT_CONNECTION_TIMEOUT = 8 * 1000;
    public static int DEFAULT_READ_TIMEOUT = 60 * 1000;

    private static ExecutorService sSendThreadPool = Executors
            .newFixedThreadPool(3);

    public static void loadImage(final String saveUrl, String url, final CallBack callBack){
        sSendThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn;
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    URL u = new URL(url);
                    conn = (HttpURLConnection) u.openConnection();
                    conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
                    conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
                    conn.setAllowUserInteraction(false);
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        inputStream = conn.getInputStream();
                        outputStream = new FileOutputStream(saveUrl);
                        int len;
                        byte[] buffer=new byte[1024];
                        while ((len=inputStream.read(buffer))!=-1) {
                            outputStream.write(buffer,0,len);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callBack != null){
                        callBack.fail(-1, e.getMessage());
                    }
                }finally {
                    try {
                        if (inputStream != null){
                            inputStream.close();
                        }

                        if (outputStream != null){
                            outputStream.close();
                        }

                        if (callBack != null){
                            callBack.ok(saveUrl);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (callBack != null){
                            callBack.fail(-1, e.getMessage());
                        }
                    }
                }
            }
        });
    }

    public interface CallBack{
        void ok(String url);
        void fail(int code, String error);
    }
}
