package com.bonniedraw.notification.model;

public class NotiMsgInfo extends NotiMsgInfoKey {
    private String message1;

    private String message2;

    private String message3;

    private String message4;

    private String message5;

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1 == null ? null : message1.trim();
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2 == null ? null : message2.trim();
    }

    public String getMessage3() {
        return message3;
    }

    public void setMessage3(String message3) {
        this.message3 = message3 == null ? null : message3.trim();
    }

    public String getMessage4() {
        return message4;
    }

    public void setMessage4(String message4) {
        this.message4 = message4 == null ? null : message4.trim();
    }

    public String getMessage5() {
        return message5;
    }

    public void setMessage5(String message5) {
        this.message5 = message5 == null ? null : message5.trim();
    }
}