package com.easemob.helpdeskdemo.domain;

import java.util.List;

/**
 * Created by liyuzhao on 16/7/15.
 */
public class CommentListResponse {

    /**
     * first : true
     * last : true
     * totalPages : 1
     * size : 10
     * number : 0
     * numberOfElements : 5
     * totalElements : 5
     * entities : [{"id":170379,"subject":"评论测试1","content":"评论测试1","creator":{"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"},"version":0,"attachments":[],"created_at":"2016-07-15T10:27:01.000Z","updated_at":"2016-07-15T10:27:01.000Z"},{"id":169374,"subject":"评论测试2","content":"评论测试2","creator":{"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"},"version":0,"attachments":[],"created_at":"2016-07-15T10:27:07.000Z","updated_at":"2016-07-15T10:27:07.000Z"},{"id":170380,"subject":"回复 cushion : 是这个样子的","content":"回复 cushion : 是这个样子的","creator":{"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"},"version":0,"attachments":[],"created_at":"2016-07-15T10:27:25.000Z","updated_at":"2016-07-15T10:27:25.000Z"},{"id":169375,"subject":"回复 ceshiok :没了","content":"回复 ceshiok :没了","creator":{"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"},"version":0,"attachments":[],"created_at":"2016-07-15T10:27:35.000Z","updated_at":"2016-07-15T10:27:35.000Z"},{"id":169376,"subject":"带附件的测试","content":"带附件的测试","creator":{"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"},"version":0,"attachments":[{"name":"classes.jar","url":"http://kefu.easemob.com/v1/Tenant/35/MediaFiles/81300a91-d72f-490e-a023-ed975e5e591cY2xhc3Nlcy5qYXI=","type":"file"},{"name":"AndroidManifest.xml","url":"http://kefu.easemob.com/v1/Tenant/35/MediaFiles/5ddfc81a-c4cd-4e0f-9db9-4f17db79cee6QW5kcm9pZE1hbmlmZXN0LnhtbA==","type":"file"}],"created_at":"2016-07-15T10:32:36.000Z","updated_at":"2016-07-15T10:32:36.000Z"}]
     */

    private boolean first;
    private boolean last;
    private int totalPages;
    private int size;
    private int number;
    private int numberOfElements;
    private int totalElements;
    /**
     * id : 170379
     * subject : 评论测试1
     * content : 评论测试1
     * creator : {"id":"7d3a27bf-f646-4d2a-bab2-c9803b4aa866","name":"ceshiok","phone":"15811251234","username":"ceshiok@qq.com","avatar":"//kefu.easemob.com/ossimages/kefu-prod-avatar.img-cn-hangzhou.aliyuncs.com/avatar/35/ab1f2fa3-bb25-4063-9c82-998eac6ba063@0-0-300-300a|300h_300w|.png"}
     * version : 0
     * attachments : []
     * created_at : 2016-07-15T10:27:01.000Z
     * updated_at : 2016-07-15T10:27:01.000Z
     */

    private List<CommentEntity> entities;

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public List<CommentEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<CommentEntity> entities) {
        this.entities = entities;
    }

}
