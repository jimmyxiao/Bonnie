package com.sctw.bonniedraw.paint;

import java.sql.Timestamp;

/**
 * Created by Fatorin on 2017/8/29.
 */

public class PaintInfo {

    private String imgName;
    private String imgAuthor;
    private String email;
    private Timestamp imgGenerateTime;
    private int imgWidth;
    private int imgHeight;

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgAuthor() {
        return imgAuthor;
    }

    public void setImgAuthor(String imgAuthor) {
        this.imgAuthor = imgAuthor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getImgGenerateTime() {
        return imgGenerateTime;
    }

    public void setImgGenerateTime(Timestamp imgGenerateTime) {
        this.imgGenerateTime = imgGenerateTime;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
}
