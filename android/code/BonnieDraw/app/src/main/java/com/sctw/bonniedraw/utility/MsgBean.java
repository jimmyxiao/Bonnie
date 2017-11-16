package com.sctw.bonniedraw.utility;

/**
 * Created by Fatorin on 2017/10/27.
 */

public class MsgBean {
    private int worksMsgId;
    private int worksId;
    private int userId;
    private String message;
    private int msgOrder;
    private String creationDate;
    private String userName;

    public int getWorksMsgId() {
        return worksMsgId;
    }

    public void setWorksMsgId(int worksMsgId) {
        this.worksMsgId = worksMsgId;
    }

    public int getWorksId() {
        return worksId;
    }

    public void setWorksId(int worksId) {
        this.worksId = worksId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgOrder() {
        return msgOrder;
    }

    public void setMsgOrder(int msgOrder) {
        this.msgOrder = msgOrder;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
