package com.wd.cloud.bse.util;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseUtil {

	private static final Pattern p1 = Pattern.compile("[\u4e00-\u9fa5]");

	public static boolean isChinese(String str) {
		Matcher m = p1.matcher(str);
		return m.find();
	}
	
	/*
	 * 去除标题标点
	 */
	public static String normalization(String titleMainName) {
		if (titleMainName != null) {
			titleMainName = titleMainName.replaceAll(
					"(?i)[^a-zA-Z0-9\u4E00-\u9FA5ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ]", "");
			titleMainName = titleMainName.toLowerCase().trim(); // 去标点 去空格 转小写
		}
		return titleMainName.trim();
	}
	
	public static String matcherFor(String value,String regex) {
		Pattern p=Pattern.compile(regex);
		Matcher matcher=p.matcher(value);
		String result="";
		if(matcher.find()){
			result=matcher.group(); 
		}
		return result;
	}
}
