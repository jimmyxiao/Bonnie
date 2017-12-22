package com.sctw.bonniedraw.bean;

/**
 * Created by Fatorin on 2017/12/22.
 */

public class TagListBean {
    private int tagId;
    private String tagName;
    private String tagEngName;
    private int tagOrder;
    private String countryCode;

    public TagListBean(int tagId, String tagName, String tagEngName, int tagOrder, String countryCode) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagEngName = tagEngName;
        this.tagOrder = tagOrder;
        this.countryCode = countryCode;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagEngName() {
        return tagEngName;
    }

    public void setTagEngName(String tagEngName) {
        this.tagEngName = tagEngName;
    }

    public int getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(int tagOrder) {
        this.tagOrder = tagOrder;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
