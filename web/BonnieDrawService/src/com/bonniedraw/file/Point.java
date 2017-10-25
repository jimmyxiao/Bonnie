package com.bonniedraw.file;

public class Point {
	private int length;
	private int fc;
	private int xPos;
	private int yPos;
	private String color;
	private int action;
	private int size;
	private int brush;
	private Integer time;
	private Integer reserve;

	public Point() {
		super();
	}

	public Point(int length, int fc, int xPos, int yPos, String color, int action, int size, int brush, Integer time, Integer reserve) {
		super();
		this.length = 0;
		this.fc = fc;
		this.xPos = 0;
		this.yPos = 0;
		this.color = "0xFF000000";
		this.action = 0;
		this.size = 0;
		this.brush = 0;
		this.time = 0;
		this.reserve = 0;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFc() {
		return fc;
	}

	public void setFc(int fc) {
		this.fc = fc;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getBrush() {
		return brush;
	}

	public void setBrush(int brush) {
		this.brush = brush;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getReserve() {
		return reserve;
	}

	public void setReserve(Integer reserve) {
		this.reserve = reserve;
	}

}
