package com.bonniedraw.notification.model;

import java.util.Date;

public class NotificationMsg {
    private Integer notiMsgId;

    private Integer notiMsgType;

    private Integer userId;

    private Integer userIdFollow;

    private Integer worksId;

    private Integer worksMsgId;

    private Integer thirdType;

    private Date creationDate;

    public Integer getNotiMsgId() {
        return notiMsgId;
    }

    public void setNotiMsgId(Integer notiMsgId) {
        this.notiMsgId = notiMsgId;
    }

    public Integer getNotiMsgType() {
        return notiMsgType;
    }

    public void setNotiMsgType(Integer notiMsgType) {
        this.notiMsgType = notiMsgType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserIdFollow() {
        return userIdFollow;
    }

    public void setUserIdFollow(Integer userIdFollow) {
        this.userIdFollow = userIdFollow;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }

    public Integer getWorksMsgId() {
        return worksMsgId;
    }

    public void setWorksMsgId(Integer worksMsgId) {
        this.worksMsgId = worksMsgId;
    }

    public Integer getThirdType() {
        return thirdType;
    }

    public void setThirdType(Integer thirdType) {
        this.thirdType = thirdType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}