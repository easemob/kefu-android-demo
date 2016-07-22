package com.easemob.helpdeskdemo.domain;

import java.util.List;

/**
 *
 */
public class TicketListResponse {
    /**
     * first : true
     * last : false
     * totalPages : 2
     * size : 10
     * number : 0
     * numberOfElements : 10
     * totalElements : 11
     * entities : [{"id":173431,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"cccc","content":"cccc","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T04:30:56.000Z","updated_at":"2016-07-14T04:30:56.000Z"},{"id":173432,"status":{"id":49068,"name":"处理中","version":0},"attachments":[],"subject":"sss","content":"sss","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":1,"created_at":"2016-07-14T04:32:51.000Z","updated_at":"2016-07-14T04:53:06.000Z"},{"id":171631,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"sss","content":"sss","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T04:49:31.000Z","updated_at":"2016-07-14T04:49:31.000Z"},{"id":171632,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"","content":"","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T04:58:56.000Z","updated_at":"2016-07-14T04:58:56.000Z"},{"id":171633,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"aa","content":"aa","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T05:08:21.000Z","updated_at":"2016-07-14T05:08:21.000Z"},{"id":171638,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"11","content":"11","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T06:07:45.000Z","updated_at":"2016-07-14T06:07:45.000Z"},{"id":171639,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"11","content":"11","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T06:16:51.000Z","updated_at":"2016-07-14T06:16:51.000Z"},{"id":173441,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"11","content":"11","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T06:17:02.000Z","updated_at":"2016-07-14T06:17:02.000Z"},{"id":173442,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"333","content":"333","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T06:18:03.000Z","updated_at":"2016-07-14T06:18:03.000Z"},{"id":173443,"status":{"id":49067,"name":"未解决","version":0},"attachments":[],"subject":"content test","content":"content test","creator":{"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"},"version":0,"created_at":"2016-07-14T06:20:00.000Z","updated_at":"2016-07-14T06:20:00.000Z"}]
     */

    private boolean first;
    private boolean last;
    private int totalPages;
    private int size;
    private int number;
    private int numberOfElements;
    private int totalElements;
    /**
     * id : 173431
     * status : {"id":49067,"name":"未解决","version":0}
     * attachments : []
     * subject : cccc
     * content : cccc
     * creator : {"id":"c601d93d-194d-44e0-bf1e-0e4a010c8823","name":"11","email":"11","phone":"33","username":"5z0889lxe0","type":"VISITOR"}
     * version : 0
     * created_at : 2016-07-14T04:30:56.000Z
     * updated_at : 2016-07-14T04:30:56.000Z
     */

    private List<TicketEntity> entities;

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

    public List<TicketEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<TicketEntity> entities) {
        this.entities = entities;
    }

}
