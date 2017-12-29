package com.sctw.bonniedraw.bean;

/**
 * Created by Fatorin on 2017/12/29.
 */

public class UserInfoBean {
    private String userName;
    private String description;
    private String profilePicture;
    private int worksNum;
    private int fansNum;
    private int followNum;
    private boolean follow;

    public String getUserName() {
        return userName;
    }

    public String getDescription() {
        return description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getWorksNum() {
        return worksNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setWorksNum(int worksNum) {
        this.worksNum = worksNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
