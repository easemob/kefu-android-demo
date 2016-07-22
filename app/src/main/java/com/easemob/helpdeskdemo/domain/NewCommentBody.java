package com.easemob.helpdeskdemo.domain;

import java.util.List;

/**
 *
 */
public class NewCommentBody {

    /**
     * subject : 评论的主题, 可选
     * content : 评论的内容
     * reply : {"id":"回复的哪条评论的id, 可选"}
     * creator : {"id":"创建这个评论的人的id","username":"创建这个comment的人的username","name":"创建这个comment的人的name","avatar":"创建这个comment的人的头像","type":"创建这个comment的人的类型, 例如是坐席还是访客","email":"电子邮件地址","phone":"电话号码","qq":"qq号码","company":"公司","description":"具体的描述信息"}
     * attachments : [{"name":"该附件的名称","url":"该附件的url","type":"附件的类型, 当前支持image和file"}]
     */

    private String subject;
    private String content;

    /**
     * id : 回复的哪条评论的id, 可选
     */
    private ReplyBean reply;
    /**
     * id : 创建这个评论的人的id
     * username : 创建这个comment的人的username
     * name : 创建这个comment的人的name
     * avatar : 创建这个comment的人的头像
     * type : 创建这个comment的人的类型, 例如是坐席还是访客
     * email : 电子邮件地址
     * phone : 电话号码
     * qq : qq号码
     * company : 公司
     * description : 具体的描述信息
     */

    private CreatorBean creator;
    /**
     * name : 该附件的名称
     * url : 该附件的url
     * type : 附件的类型, 当前支持image和file
     */

    private List<AttachmentsBean> attachments;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ReplyBean getReply() {
        return reply;
    }

    public void setReply(ReplyBean reply) {
        this.reply = reply;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public List<AttachmentsBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentsBean> attachments) {
        this.attachments = attachments;
    }

    public static class ReplyBean {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class CreatorBean {
        private String id;
        private String username;
        private String name;
        private String avatar;
        private String type;
        private String email;
        private String phone;
        private String qq;
        private String company;
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class AttachmentsBean {
        private String name;
        private String url;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
