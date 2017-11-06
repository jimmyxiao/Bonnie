package com.sctw.bonniedraw.colorpick;

import java.io.Serializable;

/**
 * Created by Fatorin on 2017/11/2.
 */

public class ColorBean implements Serializable {
    private int color;
    private boolean isSelect;

    public ColorBean(int color) {
        this.color = color;
        this.isSelect = false;
    }

    public ColorBean(int color, boolean isSelect) {
        this.color = color;
        this.isSelect = isSelect;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "ColorBean{" +
                "color=" + color +
                ", isSelect=" + isSelect +
                '}';
    }
}
