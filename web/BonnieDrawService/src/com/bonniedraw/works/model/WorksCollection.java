package com.bonniedraw.works.model;

public class WorksCollection {
    private Integer worksCollectionId;

    private Integer worksId;

    private Integer userId;

    private Integer collectionType;

    public Integer getWorksCollectionId() {
        return worksCollectionId;
    }

    public void setWorksCollectionId(Integer worksCollectionId) {
        this.worksCollectionId = worksCollectionId;
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

    public Integer getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Integer collectionType) {
        this.collectionType = collectionType;
    }
}