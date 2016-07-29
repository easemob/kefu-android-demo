package com.easemob.helpdeskdemo.domain;

/**
 * Created by liyuzhao on 16/7/13.
 */
public class NewTicketBody {

    /**
     * subject : ticket的主题, 可选, 如果没有的话, 那么默认是content的前10个字
     * content : ticket的主要内容
     * status_id : 可选, 如果没有则使用project定义的默认的status, 如果没有定义默认的status则留空
     * priority_id : 可选, 如果没有则使用project定义的默认的priority, 如果没有定义默认的priority则留空
     * category_id : 可选, 如果没有则使用project定义的默认的category, 如果没有定义默认的priority则留空
     * creator : {"name":"创建这个ticket的人的name","avatar":"创建这个ticket的人的头像(//可选)","email":"电子邮件地址","phone":"电话号码","qq":"qq号码","company":"公司","description":"具体的描述信息"}
     */

    private String subject;
    private String content;
    private String status_id;
    private String priority_id;
    private String category_id;
    /**
     * name : 创建这个ticket的人的name
     * avatar : 创建这个ticket的人的头像(//可选)
     * email : 电子邮件地址
     * phone : 电话号码
     * qq : qq号码
     * company : 公司
     * description : 具体的描述信息
     */

    private CreatorBean creator;

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

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getPriority_id() {
        return priority_id;
    }

    public void setPriority_id(String priority_id) {
        this.priority_id = priority_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public static class CreatorBean {
        private String name;
        private String avatar;
        private String email;
        private String phone;
        private String qq;
        private String company;
        private String description;

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
}
