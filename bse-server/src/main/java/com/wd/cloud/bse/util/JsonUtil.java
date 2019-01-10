package com.wd.cloud.bse.util;


import java.io.IOException;
import java.io.StringWriter;



/**
 * 将对象转json,json转对象
 * 
 * @author pan
 * 
 */
public class JsonUtil {

	public static String obj2Json(Object obj) {

		StringWriter stringWriter = new StringWriter();
		com.fasterxml.jackson.core.JsonGenerator jsonGenerator = null;
		try {
			jsonGenerator = new com.fasterxml.jackson.core.JsonFactory().createJsonGenerator(stringWriter);
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			objectMapper.writeValue(jsonGenerator, obj);
		} catch (IOException e) {
			// TODO 记录日志
			e.printStackTrace();
		} finally {
			if (null != jsonGenerator) {
				try {
					jsonGenerator.close();
				} catch (IOException e) {
					// TODO 记录日志
					e.printStackTrace();
				}
			}
		}
		return stringWriter.getBuffer().toString();
	}

	public static <T> T json2Obj(String json, Class<T> cls) {

		com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
		T o = null;
		try {
			o = mapper.readValue(json, cls);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public static String ObjToString(String[] obj) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<obj.length;i++) {
			if(i == obj.length-1) {
				sb.append(obj[i]);
			} else {
				sb.append(obj[i]+",");
			}
		}
		return sb.toString();
	}
}
