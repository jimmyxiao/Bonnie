package com.sctw.bonniedraw.bean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/10/19.
 */

public class WorkInfoBean {
    private String workId;
    private String userId;
    private String userName;
    private String title;
    private String description;
    private String imagePath;
    private int isFollowing;
    private String userImgPath;
    private Integer likeCount;
    private Integer msgCount;
    private boolean like;
    private boolean isCollection;

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

    public int getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(int isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getUserImgPath() {
        return userImgPath;
    }

    public void setUserImgPath(String userImgPath) {
        this.userImgPath = userImgPath;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public static ArrayList<WorkInfoBean> generateInfoList(JSONArray data) {
        ArrayList<WorkInfoBean> workInfoBeanList = new ArrayList<>();
        try {
            for (int x = 0; x < data.length(); x++) {
                WorkInfoBean workInfoBean = new WorkInfoBean();
                ArrayList<MsgBean> msgBeanList = new ArrayList<>();
                workInfoBean.setWorkId(data.getJSONObject(x).getString("worksId"));
                workInfoBean.setUserId(data.getJSONObject(x).getString("userId"));
                workInfoBean.setUserName(data.getJSONObject(x).getString("userName"));
                workInfoBean.setTitle(data.getJSONObject(x).getString("title"));
                workInfoBean.setImagePath(data.getJSONObject(x).getString("imagePath"));
                workInfoBean.setCollection(data.getJSONObject(x).getBoolean("collection"));
                workInfoBean.setIsFollowing(data.getJSONObject(x).getInt("isFollowing"));
                workInfoBean.setUserImgPath(data.getJSONObject(x).getString("profilePicture"));
                if (!data.getJSONObject(x).isNull("likeCount")) {
                    workInfoBean.setLikeCount(data.getJSONObject(x).getInt("likeCount"));
                } else {
                    workInfoBean.setLikeCount(0);
                }
                if (!data.getJSONObject(x).isNull("msgCount")) {
                    workInfoBean.setMsgCount(data.getJSONObject(x).getInt("msgCount"));
                } else {
                    workInfoBean.setMsgCount(0);
                }
                workInfoBean.setLike(data.getJSONObject(x).getBoolean("like"));
                workInfoBeanList.add(workInfoBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workInfoBeanList;
    }
}
