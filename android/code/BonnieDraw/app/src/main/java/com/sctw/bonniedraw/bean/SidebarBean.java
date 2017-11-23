package com.sctw.bonniedraw.bean;

/**
 * Created by Fatorin on 2017/11/23.
 */

public class SidebarBean {
    private int drawableId;
    private String title;

    public SidebarBean(int drawableId, String title) {
        this.drawableId = drawableId;
        this.title = title;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
