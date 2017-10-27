package com.sctw.bonniedraw.utility;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/10/19.
 */

public class WorkInfo {
    private String workId;
    private String userId;
    private String userName;
    private String title;
    private String description;
    private String imagePath;
    private String isFollowing;
    private String userImgPath;
    private ArrayList<Msg> msgList;
    private boolean like;

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getUserImgPath() {
        return userImgPath;
    }

    public void setUserImgPath(String userImgPath) {
        this.userImgPath = userImgPath;
    }

    public ArrayList<Msg> getMsgList() {
        return msgList;
    }

    public void setMsgList(ArrayList<Msg> msgList) {
        this.msgList = msgList;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
