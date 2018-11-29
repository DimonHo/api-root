package com.wd.cloud.bse.util;


import java.io.StringReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import com.wd.cloud.bse.vo.QueryParam;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 请求XML解析,将xml字符串转换为一个JSON对象
 * @author Administrator
 *
 */
public class ParamsAnalyze {
	
	public static QueryParam parse(String xml) throws Exception{
		return new QueryParam(analyze(xml));
	}
	
	public static JSONObject analyze(String xml) throws Exception{
		return analyze(getRoot(xml));
	}
	
	public static Element getRoot(String xml) throws Exception{
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try{
			document = saxReader.read(new StringReader(xml));
		}catch(DocumentException e){
			throw new Exception(e);
		}
		return  (Element)document.selectSingleNode("/params");
	}
	
	@SuppressWarnings("rawtypes")
	private static JSONObject analyze(Element element){
		JSONObject json = new JSONObject();
		Iterator ite =element.elementIterator();
		String name = null,valueStr = null;
		Object value = null,preValue=null;
		Element ele;
		JSONArray arr =null;
		while(ite.hasNext()){
			ele = (Element)ite.next();
			name  = ele.getName();
			if(ele.isTextOnly()){
				valueStr = ele.getText();
				if(StringUtils.isEmpty(valueStr)){//忽略空置
					continue;
				}
				value = valueStr;
			}else{
				value  = analyze(ele);
			}
			preValue = json.get(name);
			if(preValue == null){
				json.put(name, value);
			}else{
				if(preValue instanceof JSONArray){
					arr =(JSONArray) preValue;
				}else{
					arr = new JSONArray();
					arr.add(preValue);
				}
				arr.add(value);
				json.put(name, arr);
			}
		}
		return json;
	}

}
