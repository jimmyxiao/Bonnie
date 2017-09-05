package com.bonniedraw.systemsetup.model;

public class Dictionary {
    private Integer dictionaryId;

    private Integer dictionaryType;

    private Integer dictionaryCode;

    private String dictionaryName;

    private Integer dictionaryOrder;

    public Integer getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public Integer getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(Integer dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public Integer getDictionaryCode() {
        return dictionaryCode;
    }

    public void setDictionaryCode(Integer dictionaryCode) {
        this.dictionaryCode = dictionaryCode;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName == null ? null : dictionaryName.trim();
    }

    public Integer getDictionaryOrder() {
        return dictionaryOrder;
    }

    public void setDictionaryOrder(Integer dictionaryOrder) {
        this.dictionaryOrder = dictionaryOrder;
    }
}