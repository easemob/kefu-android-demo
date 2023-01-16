package com.easemob.veckit.agora.board.misc.flat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONObject;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 此类提供一个PPT转换思路，用户可依据{@see <a href="https://developer.netless.link/server-zh/home/server-conversion">ppt转换</a>}自行实现转换
 */
public class FileConverter {
    private String resource;
    private ConvertType type;
    private boolean preview;
    private double scale;
    private ImageFormat outputFormat;
    private boolean pack;
    private Region region;
    private String sdkToken;
    private String taskUuid;
    private String taskToken;
    private long interval;
    private long timeout;
    private int callId;
    private String tenantId;
    private String taskId;
    private String typeString;

    private long startTime;
    private ConverterCallbacks outCallbacks;
    private volatile ConverterStatus status = ConverterStatus.Created;

    private FileConverter(String resource,
                          ConvertType type,
                          boolean preview,
                          double scale,
                          ImageFormat outputFormat,
                          boolean pack,
                          Region region,
                          String sdkToken,
                          String taskUuid,
                          String taskToken,
                          long interval,
                          long timeout,
                          int callId,
                          String tenantId,
                          String taskId,
                          String typeString,
                          ConverterCallbacks callbacks) {
        this.resource = resource;
        this.type = type;
        this.preview = preview;
        this.scale = scale;
        this.outputFormat = outputFormat;
        this.pack = pack;
        this.region = region;
        this.sdkToken = sdkToken;
        this.taskUuid = taskUuid;
        this.taskToken = taskToken;
        this.interval = interval;
        this.timeout = timeout;
        this.outCallbacks = callbacks;
        this.callId = callId;
        this.tenantId = tenantId;
        this.taskId = taskId;
        this.typeString = typeString;
    }

    static ThreadPoolExecutor executorService;

    static {
        executorService =
                new ThreadPoolExecutor(4, 4, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                        new ConverterThreadFactory());
        executorService.allowCoreThreadTimeOut(true);
    }

    private static final class ConverterThreadFactory implements ThreadFactory {
        @Override
        public synchronized Thread newThread(Runnable runnable) {
            Thread result = new Thread(runnable, "white-sdk-converter");
            result.setPriority(Thread.MIN_PRIORITY);
            return result;
        }
    }

    private final Gson gson = new Gson();
    public void startConvertTask() {
        if (startTime != 0 && isNotFinish()) {
            return;
        }
        status = ConverterStatus.Created;
        startTime = System.currentTimeMillis();
        executorService.execute(() -> {
            if (status == ConverterStatus.Created) {
                startProgressLoop();
            }
        });
    }

    private boolean isNotFinish() {
        return !(status == ConverterStatus.Success || status == ConverterStatus.Fail);
    }

    /**
     * @return 转换状态
     */
    public ConverterStatus getStatus() {
        return status;
    }

    /**
     * @return 转换任务唯一标识
     */
    public String getTaskUuid() {
        return taskUuid;
    }

    /**
     * @return 转换任务查询token
     */
    public String getTaskToken() {
        return taskToken != null ? taskToken : sdkToken;
    }


    private ConvertType parseType(String type) {
        if ("static".equals(type)) {
            return ConvertType.Static;
        } else {
            return ConvertType.Dynamic;
        }
    }

    private String convertType(ConvertType type) {
        if (type == ConvertType.Dynamic) {
            return "dynamic";
        } else {
            return "static";
        }
    }

    private String convertRegion(Region region) {
        JsonElement regionElement = gson.toJsonTree(region);
        return regionElement.getAsString();
    }




    private void startProgressLoop() {
        long timeLimit = startTime + timeout;
        try {
            status = ConverterStatus.Checking;
            while (System.currentTimeMillis() < timeLimit) {
                if (status != ConverterStatus.Checking) {
                    break;
                }
                Log.e("tttttttttttt","be");
                checkProgress();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                }
                Log.e("tttttttttttt","af");
            }
        } catch (ConvertException e) {
            onFailure(e);
            status = ConverterStatus.Fail;
        }
        Log.e("tttttttttttt","onFailure");
        if (status != ConverterStatus.Success && status != ConverterStatus.Fail){
            onFailure(new ConvertException(ConvertErrorCode.CheckTimeout));
            status = ConverterStatus.Timeout;
        }
    }

    // Step 3: 轮询查询
    private void checkProgress() throws ConvertException {

        // typeString = static,dynamic
        if (FileConverter.this.status != ConverterStatus.Checking){
            return;
        }
        AgoraMessage.asyncFileConvertedProgress(ChatClient.getInstance().tenantId(), AgoraMessage.getToken(),
                callId, typeString, taskUuid, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        try {
                            Log.e("tttttttttttt","value = "+value);
                            JSONObject object = new JSONObject(value);
                            if (object.isNull("status")){
                                FileConverter.this.status = ConverterStatus.Fail;
                                onFailure(-1, "");
                                return;
                            }
                            String isOk = object.getString("status");
                            if (!"ok".equalsIgnoreCase(isOk)){
                                FileConverter.this.status = ConverterStatus.Fail;
                                onFailure(-1, "");
                                return;
                            }
                            if (object.isNull("entity")){
                                FileConverter.this.status = ConverterStatus.Fail;
                                onFailure(-1, "");
                                return;
                            }

                            String entity = object.getString("entity");
                            QueryInfo queryInfo = gson.fromJson(entity, QueryInfo.class);
                            ConversionInfo info = queryInfo.progress;
                            if (info == null){
                                FileConverter.this.status = ConverterStatus.Fail;
                                onFailure(-1, "");
                                return;
                            }
                            ConversionInfo.ServerConversionStatus status = queryInfo.status;
                            if (status == ConversionInfo.ServerConversionStatus.Fail || status == ConversionInfo.ServerConversionStatus.NotFound) {
                                FileConverter.this.status = ConverterStatus.Fail;
                                ConvertErrorCode code = status == ConversionInfo.ServerConversionStatus.Fail ? ConvertErrorCode.ConvertFail : ConvertErrorCode.NotFound;
                                onFailure(new ConvertException(code, queryInfo.failedReason));
                            } else if (status == ConversionInfo.ServerConversionStatus.Finished) {
                                FileConverter.this.status = ConverterStatus.Success;
                                onFinish(getPpt(info, type, queryInfo), info, queryInfo);
                            } else {
                                onProgress(info.getConvertedPercentage(), queryInfo.progress);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            FileConverter.this.status = ConverterStatus.Fail;
                            onFailure(-1, e.getMessage());
                            Log.e("tttttttttttt","Exception = "+e.toString());
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        onFailure(error, errorMsg);
                        FileConverter.this.status = ConverterStatus.Fail;
                    }
                });
    }

    private void onFinish(ConvertedFiles convertedFiles, ConversionInfo info, QueryInfo queryInfo) {
        if (outCallbacks != null) {
            outCallbacks.onFinish(convertedFiles, info, queryInfo);
        }
    }

    private void onFailure(ConvertException e) {
        if (outCallbacks != null) {
            outCallbacks.onFailure(e);
        }
    }

    private void onFailure(int code, String error) {
        if (outCallbacks != null) {
            outCallbacks.onFailure(code, error);
        }
    }

    private void onProgress(Double convertedPercentage, ConversionInfo info) {
        if (outCallbacks != null) {
            outCallbacks.onProgress(convertedPercentage, info);
        }
    }

    private ConvertedFiles getPpt(ConversionInfo info, ConvertType type, QueryInfo queryInfo) {
        int length = info.getConvertedFileList().length;
        String[] sliderURLs = new String[length];
        Scene[] scenes = new Scene[length];
        for (int i = 0; i < length; i++) {
            PptPage pptPage = info.getConvertedFileList()[i];
            sliderURLs[i] = pptPage.getSrc();
            scenes[i] = new Scene(String.valueOf(i + 1), pptPage);
        }

        ConvertedFiles files = new ConvertedFiles();
        // files.setTaskId(taskUuid);
        files.setTaskId(queryInfo.uuid);
        files.setType(type);
        files.setSlideURLs(sliderURLs);
        files.setScenes(scenes);

        return files;
    }

    public static class Builder {
        private String resource;
        private ConvertType type;
        private boolean preview = false;
        private double scale = 1.2;
        private ImageFormat outputFormat;
        private boolean pack = false;
        private Region region;
        private String sdkToken;
        private String taskUuid;
        private String taskToken;
        private long interval;
        private long timeout;
        private ConverterCallbacks callback;
        private int callId;
        private String tenantId;
        private String taskId;
        private String typeString;

        /**
         * @param resource 转换任务源文件 url
         * @return
         */
        public Builder setResource(String resource) {
            this.resource = resource;
            return this;
        }

        /**
         * @param type 转换任务类型，枚举：dynamic, static
         * @return
         */
        public Builder setType(ConvertType type) {
            this.type = type;
            return this;
        }

        /**
         * 只有动态文档转换支持预览图功能，同时生成预览图需要消耗较长时间，请根据业务需要选择
         *
         * @param preview 是否需要生成预览图，默认为 false
         * @return
         */
        public Builder setPreview(boolean preview) {
            this.preview = preview;
            return this;
        }

        public Builder setCallId(int callId){
            this.callId = callId;
            return this;
        }

        public Builder setTenantId(String tenantId){
            this.tenantId = tenantId;
            return this;
        }

        public Builder setTaskId(String taskId){
            this.taskId = taskId;
            return this;
        }

        public Builder setTypeString(String typeString){
            this.typeString = typeString;
            return this;
        }

        /**
         * 只有静态文档转换支持缩放功能
         *
         * @param scale 图片缩放比例，取值 0.1 到 3 之间的范围，默认为 1.2
         * @return
         */
        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        /**
         * 只有静态文档转换支持自定义输出格式
         * @param outputFormat 输出图片格式，默认为 png，可选参数为 png/jpg/jpeg/webp
         * @return
         */
        public Builder setOutputFormat(ImageFormat outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * 只有静态文档转换支持 pack 参数
         * @param pack 是否要生成资源包
         * @return
         */
        public Builder setPack(boolean pack) {
            this.pack = pack;
            return this;
        }

        /**
         * @param region 数据中心 ID（不填则为 cn-hz）
         * @return
         */
        public Builder setRegion(Region region) {
            this.region = region;
            return this;
        }

        /**
         * @note 由于 sdktoken 的权限过大，我们不建议将 sdktoken 暴露到前端，建议使用 sdktoken 签出 tasktoken，将 tasktoken 传到前端使用，只有拥有 tasktoken 的用户才能查询对应的任务进度。
         * @param sdkToken 用于发起文档转换任务并得到 taskuuid
         * @return
         */
        public Builder setSdkToken(String sdkToken) {
            this.sdkToken = sdkToken;
            return this;
        }

        /**
         * @param taskUuid 任务唯一标识
         * @return
         */
        public Builder setTaskUuid(String taskUuid) {
            this.taskUuid = taskUuid;
            return this;
        }

        /**
         * @param taskToken 任务查询token
         * @return
         */
        public Builder setTaskToken(String taskToken) {
            this.taskToken = taskToken;
            return this;
        }

        public Builder setPoolInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setCallback(ConverterCallbacks callback) {
            this.callback = callback;
            return this;
        }

        public FileConverter build() {
            if (typeString == null) {
                throw new RuntimeException("type should not be null");
            }

            if (region == null) {
                region = Region.cn;
            }

            if (taskUuid == null) {
                throw new RuntimeException("taskToken and taskUuid should not be null");
            }

            if (outputFormat == null) {
                outputFormat = ImageFormat.PNG;
            }

            if (timeout == 0) {
                timeout = 3 * 60 * 1000;
            }

            if (interval == 0) {
                interval = 15 * 1000; // 15 * 1000
            }
            return new FileConverter(resource, type, preview, scale, outputFormat, pack, region, sdkToken, taskUuid, taskToken, interval, timeout, callId, tenantId, taskId, typeString, callback);
        }
    }

    public class QueryInfo {
        public String uuid;
        ConvertType type;
        ConversionInfo.ServerConversionStatus status;
        String failedReason;
        ConversionInfo progress;
    }
}
