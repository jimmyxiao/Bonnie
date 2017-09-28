package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.file.Point;
import com.bonniedraw.web_api.model.ApiResponseVO;

public class DrawingPlayResponseVO extends ApiResponseVO {
	List<Point> pointList;

	public List<Point> getPointList() {
		return pointList;
	}

	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}
	
}
