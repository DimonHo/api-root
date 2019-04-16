package com.wd.cloud.bse.service.transfrom.convert;


import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessionNumConvert {

    private static final Pattern p = Pattern.compile("(\\w+)\\(([\\w\\d+]+)\\)");

    public static String convert(Map<String, Object> source) {
        Map<String, Object> accessionNums = (Map<String, Object>) source.get("shoulu_ex");
        if (accessionNums == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : accessionNums.entrySet()) {
            if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString())) {
                list.add(entry.getKey() + ":" + entry.getValue());
            }
        }
        return StringUtils.join(list, ";");
    }

    public static Map<String, String> convert(String accessionNums) {
        Map<String, String> shouluEx = new LinkedHashMap<String, String>();
        if (StringUtils.isNotBlank(accessionNums)) {
            String[] items = accessionNums.split(";");
            for (String item : items) {
                if (item.contains(":")) {
                    String[] sts = item.split(":");
                    if (sts.length == 2) {
                        if (sts[0].toLowerCase().contains("medline")) {
                            shouluEx.put("Medline", sts[1]);
                        } else {
                            shouluEx.put(sts[0], sts[1]);
                        }
                    }
                } else {
                    Matcher m = p.matcher(item);
                    if (m.matches()) {
                        shouluEx.put(m.group(1), m.group(2));
                    }
                }
            }
        }
        return shouluEx;
    }

}
