package com.wd.cloud.bse.util;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**
 * 万方期刊url转换(2018-10-16:新版、老版地址切换)
 * @author Administrator
 *
 */
public class WFJournalUrlField {
	
	public static String conver(String urlRule,Map<String, Object> params) {
		if (params.isEmpty())
			return "";
		if(params.containsKey("key")) {
			Set<Entry<String, Object>> entry = params.entrySet();
			for (Entry<String, Object> entry2 : entry) {
				if (null != urlRule) {
					if (entry2.getValue() == null) {
						throw new RuntimeException("地址转换参数缺失!");
					}
					urlRule = urlRule.replace("[" + entry2.getKey() + "]", entry2.getValue().toString());
				}
			}
		} else {
			Set<Entry<String, Object>> entry = params.entrySet();
			urlRule = "http://www.wanfangdata.com.cn/details/detail.do?";
			for (Entry<String, Object> entry2 : entry) {
				if (null != urlRule) {
					if (entry2.getValue() == null) {
						throw new RuntimeException("地址转换参数缺失!");
					}
					urlRule = urlRule + entry2.getKey() + "=" + entry2.getValue().toString() + "&";
				}
			}
		}
		return urlRule;
	}

}
