package com.sctw.bonniedraw.utility;

import java.sql.Timestamp;

/**
 * Created by Fatorin on 2017/10/27.
 */

public class Msg {
    private int worksMsgId;
    private int worksId;
    private int userId;
    private String message;
    private int msgOrder;
    private Timestamp creationDate;

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

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
}
