package com.bonniedraw.works.model;

public class WorksTag {
    private Integer worksTagId;

    private Integer worksId;

    private String tagName;

    private Integer tagOrder;

    public Integer getWorksTagId() {
        return worksTagId;
    }

    public void setWorksTagId(Integer worksTagId) {
        this.worksTagId = worksTagId;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public Integer getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(Integer tagOrder) {
        this.tagOrder = tagOrder;
    }
}