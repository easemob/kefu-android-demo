package com.easemob.helpdeskdemo.domain;

/**
 * Created by liyuzhao on 16/7/14.
 */
public class TicketMessage2 {

    /**
     * id : 173340
     * status : {"id":49067,"name":"未解决","version":0}
     * subject : liuyan
     * content : liuyan
     * creator : {"id":"11b6c5c6-5826-4245-a8b2-95b957b183db","name":"mingzi","email":"youxiang","phone":"dianhua","username":"webim-visitor-CF89T837HGJFQ2WTBETK","type":"VISITOR"}
     * version : 0
     * created_at : 2016-07-13T13:26:22.091Z
     * updated_at : 2016-07-13T13:26:22.091Z
     */

    private int id;
    /**
     * id : 49067
     * name : 未解决
     * version : 0
     */

    private StatusBean status;
    private String subject;
    private String content;
    /**
     * id : 11b6c5c6-5826-4245-a8b2-95b957b183db
     * name : mingzi
     * email : youxiang
     * phone : dianhua
     * username : webim-visitor-CF89T837HGJFQ2WTBETK
     * type : VISITOR
     */

    private CreatorBean creator;
    private int version;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

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

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public static class StatusBean {
        private int id;
        private String name;
        private int version;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static class CreatorBean {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String username;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
