package com.bonniedraw.works.model;

public class UserFolder {
    private Integer userFolderId;

    private Integer userId;

    private String folderName;

    private Integer folderOrder;

    public Integer getUserFolderId() {
        return userFolderId;
    }

    public void setUserFolderId(Integer userFolderId) {
        this.userFolderId = userFolderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName == null ? null : folderName.trim();
    }

    public Integer getFolderOrder() {
        return folderOrder;
    }

    public void setFolderOrder(Integer folderOrder) {
        this.folderOrder = folderOrder;
    }
}