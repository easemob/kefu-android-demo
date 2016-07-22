package com.easemob.helpdeskdemo.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 */
public class TicketEntity implements Parcelable{


    /**
     * id : long类型的id
     * subject : ticket的主题, 可选, 如果没有的话, 那么默认是content的前10个字
     * content : ticket的主要内容
     * status : {"id":"这个status对应的id","name":"这个status对应的名称","description":"这个status对应的描述","icon_url":"这个status对应的图标的url"}
     * priority : {"id":"这个priority对应的id","name":"这个priority对应的名称","description":"这个priority对应的描述","icon_url":"这个priority对应的图标的url"}
     * category : {"id":"这个category对应的id","name":"这个category对应的名称","description":"这个category对应的描述","icon_url":"这个category对应的图标的url"}
     * resolution : {"id":"这个resolution对应的id","name":"这个resolution对应的名称","description":"这个resolution对应的描述","icon_url":"这个resolution对应的图标的url"}
     * creator : {"id":"创建这个评论的人的id","username":"创建这个ticket的人的username","name":"创建这个ticket的人的name","avatar":"创建这个ticket的人的头像","type":"创建这个ticket的人的类型, 例如是坐席还是访客","email":"电子邮件地址","phone":"电话号码","qq":"qq号码","company":"公司","description":"具体的描述信息"}
     * assignee : {"id":"这个ticket被分配给了谁","username":"处理这个ticket的人的username","name":"处理这个ticket的人的name","avatar":"处理这个ticket的人的头像","type":"处理这个ticket的人的类型, 例如是坐席还是访客"}
     * attachments : [{"name":"该附件的名称","url":"该附件的url","type":"附件的类型, 当前支持image和file"}]
     * created_at : 创建时间
     * updated_at : 修改时间
     * resolved_at : ticket的解决时间
     * started_at : ticket的开始处理的时间
     * closed_at : ticket被关闭的时间
     */

    private String id;
    private String subject;
    private String content;
    /**
     * id : 这个status对应的id
     * name : 这个status对应的名称
     * description : 这个status对应的描述
     * icon_url : 这个status对应的图标的url
     */

    private StatusBean status;
    /**
     * id : 这个priority对应的id
     * name : 这个priority对应的名称
     * description : 这个priority对应的描述
     * icon_url : 这个priority对应的图标的url
     */

    private PriorityBean priority;
    /**
     * id : 这个category对应的id
     * name : 这个category对应的名称
     * description : 这个category对应的描述
     * icon_url : 这个category对应的图标的url
     */

    private CategoryBean category;
    /**
     * id : 这个resolution对应的id
     * name : 这个resolution对应的名称
     * description : 这个resolution对应的描述
     * icon_url : 这个resolution对应的图标的url
     */

    private ResolutionBean resolution;
    /**
     * id : 创建这个评论的人的id
     * username : 创建这个ticket的人的username
     * name : 创建这个ticket的人的name
     * avatar : 创建这个ticket的人的头像
     * type : 创建这个ticket的人的类型, 例如是坐席还是访客
     * email : 电子邮件地址
     * phone : 电话号码
     * qq : qq号码
     * company : 公司
     * description : 具体的描述信息
     */

    private CreatorBean creator;
    /**
     * id : 这个ticket被分配给了谁
     * username : 处理这个ticket的人的username
     * name : 处理这个ticket的人的name
     * avatar : 处理这个ticket的人的头像
     * type : 处理这个ticket的人的类型, 例如是坐席还是访客
     */

    private AssigneeBean assignee;
    private String created_at;
    private String updated_at;
    private String resolved_at;
    private String started_at;
    private String closed_at;
    /**
     * name : 该附件的名称
     * url : 该附件的url
     * type : 附件的类型, 当前支持image和file
     */

    private List<AttachmentsBean> attachments;

    public TicketEntity(){}
    protected TicketEntity(Parcel in) {
        id = in.readString();
        subject = in.readString();
        content = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        resolved_at = in.readString();
        started_at = in.readString();
        closed_at = in.readString();
        creator = in.readParcelable(CreatorBean.class.getClassLoader());
        status = in.readParcelable(StatusBean.class.getClassLoader());
        priority = in.readParcelable(PriorityBean.class.getClassLoader());
        category = in.readParcelable(CategoryBean.class.getClassLoader());
        resolution = in.readParcelable(ResolutionBean.class.getClassLoader());
        assignee = in.readParcelable(AssigneeBean.class.getClassLoader());

    }

    public static final Creator<TicketEntity> CREATOR = new Creator<TicketEntity>() {
        @Override
        public TicketEntity createFromParcel(Parcel in) {
            return new TicketEntity(in);
        }

        @Override
        public TicketEntity[] newArray(int size) {
            return new TicketEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public PriorityBean getPriority() {
        return priority;
    }

    public void setPriority(PriorityBean priority) {
        this.priority = priority;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public ResolutionBean getResolution() {
        return resolution;
    }

    public void setResolution(ResolutionBean resolution) {
        this.resolution = resolution;
    }

    public CreatorBean getCreator() {
        return creator;
    }

    public void setCreator(CreatorBean creator) {
        this.creator = creator;
    }

    public AssigneeBean getAssignee() {
        return assignee;
    }

    public void setAssignee(AssigneeBean assignee) {
        this.assignee = assignee;
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

    public String getResolved_at() {
        return resolved_at;
    }

    public void setResolved_at(String resolved_at) {
        this.resolved_at = resolved_at;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }

    public List<AttachmentsBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentsBean> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subject);
        dest.writeString(content);
        dest.writeString(created_at);
        dest.writeString(updated_at);
        dest.writeString(resolved_at);
        dest.writeString(started_at);
        dest.writeString(closed_at);
        dest.writeParcelable(creator, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(status, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(priority, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(category, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(resolution, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(assignee, PARCELABLE_WRITE_RETURN_VALUE);

    }

    public static class StatusBean implements Parcelable {
        private String id;
        private String name;
        private String description;
        private String icon_url;

        public StatusBean(){}

        protected StatusBean(Parcel in) {
            id = in.readString();
            name = in.readString();
            description = in.readString();
            icon_url = in.readString();
        }

        public static final Creator<StatusBean> CREATOR = new Creator<StatusBean>() {
            @Override
            public StatusBean createFromParcel(Parcel in) {
                return new StatusBean(in);
            }

            @Override
            public StatusBean[] newArray(int size) {
                return new StatusBean[size];
            }
        };

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(description);
            dest.writeString(icon_url);
        }
    }

    public static class PriorityBean implements Parcelable{
        private String id;
        private String name;
        private String description;
        private String icon_url;

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.description);
            dest.writeString(this.icon_url);
        }

        public PriorityBean() {
        }

        protected PriorityBean(Parcel in) {
            this.id = in.readString();
            this.name = in.readString();
            this.description = in.readString();
            this.icon_url = in.readString();
        }

        public static final Creator<PriorityBean> CREATOR = new Creator<PriorityBean>() {
            @Override
            public PriorityBean createFromParcel(Parcel source) {
                return new PriorityBean(source);
            }

            @Override
            public PriorityBean[] newArray(int size) {
                return new PriorityBean[size];
            }
        };
    }

    public static class CategoryBean implements Parcelable {
        private String id;
        private String name;
        private String description;
        private String icon_url;

        public CategoryBean(){}
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.description);
            dest.writeString(this.icon_url);
        }

        protected CategoryBean(Parcel in) {
            this.id = in.readString();
            this.name = in.readString();
            this.description = in.readString();
            this.icon_url = in.readString();
        }

        public static final Creator<CategoryBean> CREATOR = new Creator<CategoryBean>() {
            @Override
            public CategoryBean createFromParcel(Parcel source) {
                return new CategoryBean(source);
            }

            @Override
            public CategoryBean[] newArray(int size) {
                return new CategoryBean[size];
            }
        };
    }

    public static class ResolutionBean implements Parcelable {
        private String id;
        private String name;
        private String description;
        private String icon_url;

        public ResolutionBean(){}
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeString(this.description);
            dest.writeString(this.icon_url);
        }

        protected ResolutionBean(Parcel in) {
            this.id = in.readString();
            this.name = in.readString();
            this.description = in.readString();
            this.icon_url = in.readString();
        }

        public static final Creator<ResolutionBean> CREATOR = new Creator<ResolutionBean>() {
            @Override
            public ResolutionBean createFromParcel(Parcel source) {
                return new ResolutionBean(source);
            }

            @Override
            public ResolutionBean[] newArray(int size) {
                return new ResolutionBean[size];
            }
        };
    }

    public static class CreatorBean implements Parcelable {
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

        public CreatorBean(){}

        protected CreatorBean(Parcel in) {
            id = in.readString();
            username = in.readString();
            name = in.readString();
            avatar = in.readString();
            type = in.readString();
            email = in.readString();
            phone = in.readString();
            qq = in.readString();
            company = in.readString();
            description = in.readString();
        }

        public static final Creator<CreatorBean> CREATOR = new Creator<CreatorBean>() {
            @Override
            public CreatorBean createFromParcel(Parcel in) {
                return new CreatorBean(in);
            }

            @Override
            public CreatorBean[] newArray(int size) {
                return new CreatorBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(username);
            dest.writeString(name);
            dest.writeString(avatar);
            dest.writeString(type);
            dest.writeString(email);
            dest.writeString(phone);
            dest.writeString(qq);
            dest.writeString(company);
            dest.writeString(description);
        }
    }

    public static class AssigneeBean implements Parcelable {
        private String id;
        private String username;
        private String name;
        private String avatar;
        private String type;

        public AssigneeBean(){}

        protected AssigneeBean(Parcel in) {
            id = in.readString();
            username = in.readString();
            name = in.readString();
            avatar = in.readString();
            type = in.readString();
        }

        public static final Creator<AssigneeBean> CREATOR = new Creator<AssigneeBean>() {
            @Override
            public AssigneeBean createFromParcel(Parcel in) {
                return new AssigneeBean(in);
            }

            @Override
            public AssigneeBean[] newArray(int size) {
                return new AssigneeBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(username);
            dest.writeString(name);
            dest.writeString(avatar);
            dest.writeString(type);
        }
    }

    public static class AttachmentsBean implements Parcelable {
        private String name;
        private String url;
        private String type;

        public AttachmentsBean(){}

        protected AttachmentsBean(Parcel in) {
            name = in.readString();
            url = in.readString();
            type = in.readString();
        }

        public static final Creator<AttachmentsBean> CREATOR = new Creator<AttachmentsBean>() {
            @Override
            public AttachmentsBean createFromParcel(Parcel in) {
                return new AttachmentsBean(in);
            }

            @Override
            public AttachmentsBean[] newArray(int size) {
                return new AttachmentsBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(url);
            dest.writeString(type);
        }
    }
}
