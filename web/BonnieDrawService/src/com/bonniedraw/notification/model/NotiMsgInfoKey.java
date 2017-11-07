package com.bonniedraw.notification.model;

public class NotiMsgInfoKey {
    private Integer notiMsgType;

    private String languageCode;

    public Integer getNotiMsgType() {
        return notiMsgType;
    }

    public void setNotiMsgType(Integer notiMsgType) {
        this.notiMsgType = notiMsgType;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode == null ? null : languageCode.trim();
    }
}