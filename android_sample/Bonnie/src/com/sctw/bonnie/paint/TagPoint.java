package com.sctw.bonnie.paint;

public class TagPoint {
	private float fPosX = 0;
	private float fPosY = 0;
	private int iColor = 0;
	private int iTouchType = 0;

	public float get_fPosX() {
		return fPosX;
	}

	public float get_fPosY() {
		return fPosY;
	}

	public int get_iColor() {
		return iColor;
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

	public void set_iColor(int in_iColor) {
		iColor = in_iColor;
	}

	public void set_iTouchType(int in_iTouchType) {
		iTouchType = in_iTouchType;
	}
}