package com.bonniedraw.works.model;

public class WorksLike {
    private Integer worksLikeId;

    private Integer worksId;

    private Integer userId;

    private Integer likeType;

    public Integer getWorksLikeId() {
        return worksLikeId;
    }

    public void setWorksLikeId(Integer worksLikeId) {
        this.worksLikeId = worksLikeId;
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

    public Integer getLikeType() {
        return likeType;
    }

    public void setLikeType(Integer likeType) {
        this.likeType = likeType;
    }
}