package com.sctw.bonniedraw.utility;

/**
 * Created by Fatorin on 2017/8/30.
 */

public class WorkItem {
    String name;
    int imgId;

    public WorkItem(String name, int imgId) {
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
