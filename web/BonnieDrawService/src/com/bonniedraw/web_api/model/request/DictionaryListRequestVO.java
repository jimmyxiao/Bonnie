package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class DictionaryListRequestVO extends ApiRequestVO {
	private int dictionaryType;
	private Integer dictionaryID;

	public int getDictionaryType() {
		return dictionaryType;
	}

	public void setDictionaryType(int dictionaryType) {
		this.dictionaryType = dictionaryType;
	}

	public Integer getDictionaryID() {
		return dictionaryID;
	}

	public void setDictionaryID(Integer dictionaryID) {
		this.dictionaryID = dictionaryID;
	}

}
