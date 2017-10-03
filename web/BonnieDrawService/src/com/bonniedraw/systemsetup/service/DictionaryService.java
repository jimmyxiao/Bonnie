package com.bonniedraw.systemsetup.service;

import java.util.List;

import com.bonniedraw.systemsetup.model.Dictionary;

public interface DictionaryService {
	public List<Dictionary> getDictionaryList(int dictionaryType, Integer dictionaryId);
}
