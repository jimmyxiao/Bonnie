package com.sctw.bonniedraw.bean;

/**
 * Created by Fatorin on 2017/12/14.
 */

public class FriendBean {
    private String userName;
    private int userId;
    private String profilePicture;

    public FriendBean(String userName, int userId, String profilePicture) {
        this.userName = userName;
        this.userId = userId;
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
