package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.systemsetup.model.Dictionary;
import com.bonniedraw.web_api.model.ApiResponseVO;

public class DictionaryListResponseVO extends ApiResponseVO {
	private List<Dictionary> dictionaryList;

	public List<Dictionary> getDictionaryList() {
		return dictionaryList;
	}

	public void setDictionaryList(List<Dictionary> dictionaryList) {
		this.dictionaryList = dictionaryList;
	}

}
