package com.sctw.bonniedraw.paint;

/**
 * Created by Fatorin on 2017/8/30.
 */

public class PaintControlItem {
    String name;
    int imgId;

    public PaintControlItem(String name, int imgId) {
        this.name = name;
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
