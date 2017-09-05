package com.bonniedraw.works.model;

import java.util.Date;

public class WorksMsg {
    private Integer worksMsgId;

    private Integer worksId;

    private Integer userId;

    private String message;

    private Integer msgOrder;

    private Date creationDate;

    public Integer getWorksMsgId() {
        return worksMsgId;
    }

    public void setWorksMsgId(Integer worksMsgId) {
        this.worksMsgId = worksMsgId;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Integer getMsgOrder() {
        return msgOrder;
    }

    public void setMsgOrder(Integer msgOrder) {
        this.msgOrder = msgOrder;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}