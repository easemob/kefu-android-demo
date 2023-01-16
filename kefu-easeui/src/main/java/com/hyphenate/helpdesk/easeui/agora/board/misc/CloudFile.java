package com.hyphenate.helpdesk.easeui.agora.board.misc;


public class CloudFile {
    public String type;
    public String name;
    public String url;

    // for test, Production environments use separate classes as Doc, Image
    // for doc
    public String taskUUID;
    public String taskToken;

    // for image
    public Integer width;
    public Integer height;

    public String typeString;

    public CloudFile(){

    }


    public String getTypeString() {
        return typeString;
    }

    // 是否需要转换
    public String getConvertedTpe(){
        if ("pptx".equals(type)) {
            return "dynamic";
        }

        return "static";
    }

    public boolean isConvertedType(){
        return "pptx".equals(type) || "ppt".equals(type) || "pdf".equals(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setTaskToken(String taskToken) {
        this.taskToken = taskToken;
    }

    public void setTaskUUID(String taskUUID) {
        this.taskUUID = taskUUID;
    }

    public String getTaskToken() {
        return taskToken;
    }

    public String getTaskUUID() {
        return taskUUID;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }


    public String toString() {
        return "CloudFile{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", taskUUID='" + taskUUID + '\'' +
                ", taskToken='" + taskToken + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
