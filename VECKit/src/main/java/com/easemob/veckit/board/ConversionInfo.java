package com.easemob.veckit.board;

/**
 * 内部类，文档中隐藏
 */
public class ConversionInfo  extends WhiteObject{

    public enum ServerConversionStatus {
        Waiting,
        Converting,
        NotFound,
        Finished,
        Fail
    }

    enum ServerConversionStep {
        // 资源提取
        Extracting,
        // 资源打包
        Packaging,
        // 生成预览图
        GeneratingPreview,
        // 媒体转码
        MediaTranscode,
    }

    public PptPage[] getConvertedFileList() {
        return convertedFileList;
    }

    public String getReason() {
        return reason;
    }

    public ServerConversionStatus getConvertStatus() {
        return convertStatus;
    }

    public void setConvertStatus(ServerConversionStatus status) {
        convertStatus = status;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getTotalPageSize() {
        return totalPageSize;
    }

    public Integer getConvertedPageSize() {
        return convertedPageSize;
    }

    public Double getConvertedPercentage() {
        return convertedPercentage;
    }

    private PptPage[] convertedFileList;
    private String reason;
    private ServerConversionStatus convertStatus;
    private String prefix;
    private Integer totalPageSize;
    private Integer convertedPageSize;
    private Double convertedPercentage;
    private ServerConversionStep currentStep;
}