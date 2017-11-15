package com.sctw.bonniedraw.utility;

import org.json.JSONArray;
import org.json.JSONException;

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
    private int isFollowing;
    private String userImgPath;
    private Integer likeCount;
    private ArrayList<Msg> msgList;
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

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public static ArrayList<WorkInfo> generateInfoList(JSONArray data) {
        ArrayList<WorkInfo> workInfoList = new ArrayList<>();
        try {
            for (int x = 0; x < data.length(); x++) {
                WorkInfo workInfo = new WorkInfo();
                ArrayList<Msg> msgList = new ArrayList<>();
                workInfo.setWorkId(data.getJSONObject(x).getString("worksId"));
                workInfo.setUserId(data.getJSONObject(x).getString("userId"));
                workInfo.setUserName(data.getJSONObject(x).getString("userName"));
                workInfo.setTitle(data.getJSONObject(x).getString("title"));
                workInfo.setImagePath(data.getJSONObject(x).getString("imagePath"));
                workInfo.setCollection(data.getJSONObject(x).getBoolean("collection"));
                workInfo.setIsFollowing(data.getJSONObject(x).getInt("isFollowing"));
                workInfo.setUserImgPath(data.getJSONObject(x).getString("profilePicture"));
                if (!data.getJSONObject(x).isNull("likeCount")) {
                    workInfo.setLikeCount(data.getJSONObject(x).getInt("likeCount"));
                } else {
                    workInfo.setLikeCount(0);
                }
                workInfo.setLike(data.getJSONObject(x).getBoolean("like"));
                workInfo.setMsgList(msgList);
                workInfoList.add(workInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workInfoList;
    }
}
