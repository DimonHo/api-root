package com.wd.cloud.bse.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hnlat.esmapping.BeanDescriptor;
import com.hnlat.esmapping.BeanProperty;
import com.hnlat.esmapping.MapperException;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.Proceedings;
import com.wd.cloud.bse.data.ResourceIndex;
import com.wd.cloud.bse.vo.DocType;
import org.elasticsearch.search.SearchHit;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TypeUtils {

    private static final Object lock = new Object();

    private TypeUtils() {
    }


    public static ResourceIndex<Document> hitMapper(final SearchHit hit) {
        String _id = hit.getId();
        Map<String, Object> source = hit.getSource();
        source.put("_id", _id);
        source.put("version", hit.getVersion());
        DocType type = DocType.getFromSrouce(source);
        Class<?> classs = Proceedings.class;
        try {
            classs = Class.forName("com.wd.cloud.bse.data." + type.name());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return parseObject(JSONUtil.parseObj(source), ResourceIndex.class, classs);
    }

    public static <T> T parseObject(JSONObject json, Class<T> cls, Class<?> genenicCls) {
        java.lang.reflect.Type[] parameterizedTypes = null;
        if (genenicCls != null) {
            parameterizedTypes = new java.lang.reflect.Type[1];
            parameterizedTypes[0] = genenicCls;
        }
        BeanDescriptor descriptor = new BeanDescriptor(cls, parameterizedTypes);
        Object target = descriptor.newInstance();
        try {
            copyProperties((Map) json, target, descriptor, false);
        } catch (Exception e) {
            throw new MapperException("字段设置失败:" + descriptor.getType().getSimpleName(), e);
        }
        return (T) target;
    }

    private static void copyProperties(Map<String, Object> map, Object target, BeanDescriptor descriptor, boolean toEs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<BeanProperty> properties = descriptor.getProperties();
        for (BeanProperty property : properties) {
            if (toEs && property.getPropertyName() == null) {
                continue;
            }
            String propertyName = property.getFieldName();
            Object value = map.get(propertyName);
            if (value != null) {
                if (property.getPropertyBeanDescriptor() == null) {
                    property.set(target, value);
                } else {//复合对象
                    if (value instanceof Collection) {
                        List<Object> obj = new ArrayList<Object>();
                        Iterator ite = ((Collection) value).iterator();
                        while (ite.hasNext()) {
                            Object subObj = property.getPropertyBeanDescriptor().newInstance();
                            copyProperties((Map<String, Object>) ite.next(), subObj, property.getPropertyBeanDescriptor(), toEs);
                            obj.add(subObj);
                        }
                        property.set(target, obj);
                    } else {//普通对象
                        Object subObj = property.getPropertyBeanDescriptor().newInstance();
                        copyProperties((Map<String, Object>) value, subObj, property.getPropertyBeanDescriptor(), toEs);
                        property.set(target, subObj);
                    }
                }
            }
        }
    }

}
