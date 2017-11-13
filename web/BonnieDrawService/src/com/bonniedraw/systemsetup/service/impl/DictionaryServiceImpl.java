package com.bonniedraw.systemsetup.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.systemsetup.dao.DictionaryMapper;
import com.bonniedraw.systemsetup.model.Dictionary;
import com.bonniedraw.systemsetup.service.DictionaryService;

@Service
public class DictionaryServiceImpl extends BaseService implements DictionaryService {

	@Autowired
	DictionaryMapper dictionaryMapper;

	@Override
	public List<Dictionary> getDictionaryList(int dictionaryType,	Integer dictionaryId) {
		List<Dictionary> dictionaryList = new ArrayList<Dictionary>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dictionaryType", dictionaryType);
		paramMap.put("dictionaryId", dictionaryId);
		dictionaryList = dictionaryMapper.queryDictionaryList(paramMap);
		return dictionaryList;
	}

	@Override
	public boolean isExistByLanguageCode(String languageCode) {
		Dictionary dictionary = dictionaryMapper.selectByDictionaryCode(languageCode);
		if(dictionary!=null){
			return true;
		}
		return false;
	}
	
}
