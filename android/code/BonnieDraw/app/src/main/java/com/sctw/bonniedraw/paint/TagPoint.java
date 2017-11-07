package com.sctw.bonniedraw.paint;

public class TagPoint {

    private static int tagCode = 0xA101;
    private int iPosX = 0;
    private int iPosY = 0;
    private int iColor = 0;
    private int iAction = 0;
    private int iSize = 0;
    private int iBrush = 0;
    private int iTime = 0;
    private int iReserved = 0;

    public static int getTagCode() {
        return tagCode;
    }

    public int get_iPosX() {
        return iPosX;
    }

    public void set_iPosX(int iPosX) {
        this.iPosX = iPosX;
    }

    public int get_iPosY() {
        return iPosY;
    }

    public void set_iPosY(int iPosY) {
        this.iPosY = iPosY;
    }

    public int get_iColor() {
        return iColor;
    }

    public void set_iColor(int iColor) {
        this.iColor = iColor;
    }

    public int get_iAction() {
        return iAction;
    }

    public void set_iAction(int iAction) {
        this.iAction = iAction;
    }

    public int get_iSize() {
        return iSize;
    }

    public void set_iSize(int iSize) {
        this.iSize = iSize;
    }

    public int get_iBrush() {
        return iBrush;
    }

    public void set_iBrush(int iBrush) {
        this.iBrush = iBrush;
    }

    public int get_iTime() {
        return iTime;
    }

    public void set_iTime(int iTime) {
        this.iTime = iTime;
    }

    public int get_iReserved() {
        return iReserved;
    }

    public void set_iReserved(int iReserved) {
        this.iReserved = iReserved;
    }
}