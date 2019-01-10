package com.wd.cloud.bse.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 工具包
 * @author Administrator
 *
 */
public class CommUtil {
	
	/**
	 * list去重
	 * @param list
	 * @return
	 */
	public static <T> List<T> replaceRepeat(List<T> list) {
		Set<T> set = new HashSet(list);
		List<T> lists = new ArrayList<>();
		for (T str : set) {  
			if(str instanceof String) {
				if(StringUtils.isNotEmpty(str.toString())) {
					str = (T) str.toString().trim();
					lists.add(str);
				}
			} else {
				lists.add(str);
			}
		}
		return lists;
	}
	/**
	 * list去重并转为string
	 * @param list
	 * @return
	 */
	public static <T> String repeatToString(List<T> list) {
		Set<T> set = new HashSet(list);
		List<T> lists = new ArrayList<>();
		String val = "";
		for (T str : set) {  
			if(str instanceof String) {
				str = (T) str.toString().trim();
			}
			if(!lists.contains(str)) {
				lists.add(str);
			}
		}
		for (T t : lists) {
			val = val + ";" + t;
		}
		return val.replaceFirst(";", "");
	}

}
