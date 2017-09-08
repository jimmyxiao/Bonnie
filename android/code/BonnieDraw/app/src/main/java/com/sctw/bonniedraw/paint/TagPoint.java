package com.sctw.bonniedraw.paint;

public class TagPoint {

    static int tagCode = 0xA101;
    int iPosX = 0;
    int iPosY = 0;
    int iColor = 0;
    int iAction = 0;
    int iSize = 0;
    int iPaintType = 0;
    int iReserved = 0;
    int iOther = 0;

    public int getTagCode() {
        return tagCode;
    }

    public int getiPosX() {
        return iPosX;
    }

    public void setiPosX(int iPosX) {
        this.iPosX = iPosX;
    }

    public int getiPosY() {
        return iPosY;
    }

    public void setiPosY(int iPosY) {
        this.iPosY = iPosY;
    }

    public int getiColor() {
        return iColor;
    }

    public void setiColor(int iColor) {
        this.iColor = iColor;
    }

    public int getiAction() {
        return iAction;
    }

    public void setiAction(int iAction) {
        this.iAction = iAction;
    }

    public int getiSize() {
        return iSize;
    }

    public void setiSize(int iSize) {
        this.iSize = iSize;
    }

    public int getiPaintType() {
        return iPaintType;
    }

    public void setiPaintType(int iPaintType) {
        this.iPaintType = iPaintType;
    }

    public int getiReserved() {
        return iReserved;
    }

    public void setiReserved(int iReserved) {
        this.iReserved = iReserved;
    }

    public int getiOther() {
        return iOther;
    }

    public void setiOther(int iOther) {
        this.iOther = iOther;
    }

    @Override
    public String toString() {
        return "TagPoint{" + "iColor=" + iColor +
                ", iAction=" + iAction + "}";
    }
}