package com.bonniedraw.commodityinfo.model;

public class CommodityInfo {
    private Integer commodityId;

    private Integer worksId;

    private String commodityUrl;

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public Integer getWorksId() {
        return worksId;
    }

    public void setWorksId(Integer worksId) {
        this.worksId = worksId;
    }

    public String getCommodityUrl() {
        return commodityUrl;
    }

    public void setCommodityUrl(String commodityUrl) {
        this.commodityUrl = commodityUrl == null ? null : commodityUrl.trim();
    }
}