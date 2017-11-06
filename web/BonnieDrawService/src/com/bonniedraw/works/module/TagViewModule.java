package com.bonniedraw.works.module;

import java.util.List;

import com.bonniedraw.works.model.WorksTag;

public class TagViewModule extends WorksTag {
	private int count;
	private List<WorksTag> worksIdList;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public List<WorksTag> getWorksIdList() {
		return worksIdList;
	}
	
	public void setWorksIdList(List<WorksTag> worksIdList) {
		this.worksIdList = worksIdList;
	}

}
