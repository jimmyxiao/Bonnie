package com.bonniedraw.works.model;

public class WorksCategory {
    private Integer worksCategoryId;

    private Integer categoryId;

    private Integer worksId;

    public Integer getWorksCategoryId() {
        return worksCategoryId;
    }

    public void setWorksCategoryId(Integer worksCategoryId) {
        this.worksCategoryId = worksCategoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }
}