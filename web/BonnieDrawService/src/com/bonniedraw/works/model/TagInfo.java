package com.bonniedraw.works.model;

public class TagInfo {
    private Integer tagId;

    private String tagName;

    private String tagEngName;

    private Integer tagOrder;

    private String countryCode;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public String getTagEngName() {
        return tagEngName;
    }

    public void setTagEngName(String tagEngName) {
        this.tagEngName = tagEngName == null ? null : tagEngName.trim();
    }

    public Integer getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(Integer tagOrder) {
        this.tagOrder = tagOrder;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode == null ? null : countryCode.trim();
    }
}