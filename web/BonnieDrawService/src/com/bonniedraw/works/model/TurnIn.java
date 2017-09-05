package com.bonniedraw.works.model;

import java.util.Date;

public class TurnIn {
    private Integer turnInId;

    private Integer userId;

    private Integer worksId;

    private Integer turnInType;

    private String description;

    private Integer status;

    private Date creationDate;

    public Integer getTurnInId() {
        return turnInId;
    }

    public void setTurnInId(Integer turnInId) {
        this.turnInId = turnInId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }

    public Integer getTurnInType() {
        return turnInType;
    }

    public void setTurnInType(Integer turnInType) {
        this.turnInType = turnInType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}