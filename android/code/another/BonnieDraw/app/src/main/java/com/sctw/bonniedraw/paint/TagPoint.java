package com.sctw.bonniedraw.paint;

import android.graphics.Paint;

import java.io.Serializable;

public class TagPoint implements Serializable{

	private static final long serialVersionUID = 1730922799865354210L;

	private float fPosX = 0;
	private float fPosY = 0;
	private Paint iPaint = new Paint();
	private int iTouchType = 0;

	public float get_fPosX() {
		return fPosX;
	}

	public float get_fPosY() {
		return fPosY;
	}

	public Paint get_iPaint() {
		return iPaint;
	}

	public int get_iTouchType() {
		return iTouchType;
	}

	// SET *********************************************************************

	public void set_fPosX(float in_fPosX) {
		fPosX = in_fPosX;
	}

	public void set_fPosY(float in_fPosY) {
		fPosY = in_fPosY;
	}

	public void set_iPaint(Paint in_iPaint) {
		iPaint = in_iPaint;
	}

	public void set_iTouchType(int in_iTouchType) {
		iTouchType = in_iTouchType;
	}

    @Override
    public String toString() {
        return "TagPoint{" +
                "fPosX=" + fPosX +
                ", fPosY=" + fPosY +
                ", iPaint=" + iPaint +
                ", iTouchType=" + iTouchType +
                '}';
    }
}