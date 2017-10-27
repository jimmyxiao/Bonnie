package com.sctw.bonniedraw.utility;


public class TagPoint {

    static int tagCode = 0xA101;
    int iPosX = 0;
    int iPosY = 0;
    int iColor = 0;
    int iAction = 0;
    int iSize = 0;
    int iBrush = 0;
    int iTime = 0;
    int iReserved = 0;

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