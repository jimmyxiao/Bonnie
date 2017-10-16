package com.bonniedraw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashTagUtil {
	
	public static List<String> extractSharpTag(String description){
		List<String> strs=new ArrayList<String>();
		Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
		Matcher mat = MY_PATTERN.matcher(description);
		while (mat.find()) {
			  strs.add(mat.group(1));
		}
		return strs;
	}
	
}
