package com.wd.cloud.searchserver.util;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 将对象转json,json转对象
 *
 * @author pan
 */
public class JsonUtil {

    public static String obj2Json(Object obj) {

        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = null;
        try {
            jsonGenerator = new JsonFactory().createJsonGenerator(stringWriter);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(jsonGenerator, obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != jsonGenerator) {
                try {
                    jsonGenerator.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringWriter.getBuffer().toString();
    }

    public static <T> T json2Obj(String json, Class<T> cls) {

        ObjectMapper mapper = new ObjectMapper();
        T o = null;
        try {
            o = mapper.readValue(json, cls);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
