package com.easemob.veckit.agora.board.misc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.easemob.veckit.utils.Utils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * a singleton class to provider mock data
 *
 * @author fenglibin
 */
public class Repository {
    private static Repository instance;

    private Context context;
    private String TEST_CLOUD_FILES_JSON = "" +
            "[" +
            "    {\n" +
            "        \"type\":\"pdf\",\n" +
            "        \"name\":\"开始使用 Flat.pdf\",\n" +
            "        \"url\":\"https://flat-storage.oss-accelerate.aliyuncs.com/cloud-storage/2022-02/15/09faea1a-42f2-4ef6-a40d-7866cc5e1104/09faea1a-42f2-4ef6-a40d-7866cc5e1104.pdf\",\n" +
            "        \"taskUUID\":\"fddaeb908e0b11ecb94f39bd66b92986\",\n" +
            "        \"taskToken\":\"NETLESSTASK_YWs9NWJod2NUeXk2MmRZWC11WiZub25jZT1mZTFlZjk3MC04ZTBiLTExZWMtYTMzNS01MWEyMGJkNzRiZjYmcm9sZT0yJnNpZz1jZGQwMzMyZTFlZTkwNGEyNjhlMjQ0NDc0NWQ4MTY0ZTAzNzNiOTIxZmI4ZDY0YTE0MTJiZTU5MmUwMjM3MzM4JnV1aWQ9ZmRkYWViOTA4ZTBiMTFlY2I5NGYzOWJkNjZiOTI5ODY\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"type\":\"pptx\",\n" +
            "        \"name\":\"Get Started with Flat.pptx\",\n" +
            "        \"url\":\"https://flat-storage.oss-accelerate.aliyuncs.com/cloud-storage/2022-02/15/d9e8a040-5b44-4867-b4ea-dcd5551dd5a8/d9e8a040-5b44-4867-b4ea-dcd5551dd5a8.pptx\",\n" +
            "        \"taskUUID\":\"feae41208e0b11ecb954e907f43a0c2c\",\n" +
            "        \"taskToken\":\"NETLESSTASK_YWs9NWJod2NUeXk2MmRZWC11WiZub25jZT1mZWI5YjJkMC04ZTBiLTExZWMtYTMzNS01MWEyMGJkNzRiZjYmcm9sZT0yJnNpZz00MDc2MjU2YmIwNzI3YmU1NWUxMGQ1YmMxOTI1ZjNjZWZlMDIyZjE3Yzg2MzU4MWM3MjQzZDdhZGQ0MzVkOGM4JnV1aWQ9ZmVhZTQxMjA4ZTBiMTFlY2I5NTRlOTA3ZjQzYTBjMmM\"" +
            "    },\n" +
            "    {\n" +
            "        \"type\":\"mp4\",\n" +
            "        \"name\":\"oceans.mp4\",\n" +
            "        \"url\":\"https://flat-storage.oss-accelerate.aliyuncs.com/cloud-storage/2022-02/15/55509848-5437-463e-b52c-f81d1319c837/55509848-5437-463e-b52c-f81d1319c837.mp4\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"type\":\"png\",\n" +
            "        \"name\":\"lena_color.png\",\n" +
            "        \"url\":\"https://flat-storage.oss-accelerate.aliyuncs.com/cloud-storage/2022-02/15/ebe8320a-a90e-4e03-ad3a-a5dc06ae6eda/ebe8320a-a90e-4e03-ad3a-a5dc06ae6eda.png\",\n" +
            "        \"width\": 512,\n" +
            "        \"height\": 512\n" +
            "    },\n" +
            "    {\n" +
            "        \"type\":\"png\",\n" +
            "        \"name\":\"lena_gray.png\",\n" +
            "        \"url\":\"https://flat-storage.oss-accelerate.aliyuncs.com/cloud-storage/2022-02/15/8d487d84-e527-4760-aeb6-e13235fd541f/8d487d84-e527-4760-aeb6-e13235fd541f.png\",\n" +
            "        \"width\": 512,\n" +
            "        \"height\": 512\n" +
            "    }\n" +
            "]";

    private Repository() {
    }

    public synchronized static Repository get() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public void getRemoteData(int delay, final Callback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(new Object());
            }
        }, delay);
    }

    public List<CloudFile> getCloudFiles() {
        Type type = new TypeToken<ArrayList<CloudFile>>() {
        }.getType();
        ArrayList<CloudFile> result = Utils.fromJson(TEST_CLOUD_FILES_JSON, type);
        return result;
    }

    interface Callback {
        void onSuccess(Object object);

        void onFailure(Exception e);
    }
}
