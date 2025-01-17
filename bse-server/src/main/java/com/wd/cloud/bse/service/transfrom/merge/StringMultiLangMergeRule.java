package com.wd.cloud.bse.service.transfrom.merge;


import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;
import com.weidu.common.util.TextUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;


public class StringMultiLangMergeRule extends MergeRule<String> {

    private static String spliter = "#&#&#";

    @Override
    protected String name() {
        return "stringMultiLangMerge";
    }

    @Override
    protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
        String str = "";
        Map<Integer, List<String>> lanMap = new HashMap<Integer, List<String>>();
        List<String> list;
        String[] datas;
        Integer lang;
        for (String data : dataList) {
            //处理4e89186e120143410eb2dc597501数据
            data = data.replaceAll("《;》", " ");
            datas = data.split(spliter + "|#&amp;#&amp;#");
            for (String d : datas) {
                d = d.replace("结果输出: 本页 所有记录 Email 打印 下载 结果输出: 本页 所有记录 Email 打印 下载", "");
                if (StringUtils.isBlank(d)) {
                    continue;
                }

                if (TextUtils.hasChineseChar(d)) {
                    lang = 1;
                } else {
                    lang = 2;
                }

                if (lanMap.containsKey(lang)) {
                    list = lanMap.get(lang);
                } else {
                    list = new ArrayList<String>();
                    lanMap.put(lang, list);
                }
                list.add(d.trim());
            }
        }

        if (lanMap.containsKey(1)) {
            str = getMaxNumStr(lanMap.get(1));
        }

        if (lanMap.containsKey(2)) {
            if (!"".equals(str)) {
                str += spliter;
            }
            str += getMaxNumStr(lanMap.get(2));
        }
        return str;
    }

    private String getMaxNumStr(List<String> dataList) {
        String str = "";
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String data : dataList) {
            map.put(data, map.getOrDefault(data, 0) + 1);
        }

        if (map.size() > 0) {
            List<Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new Comparator<Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                    int cmp = o2.getValue().compareTo(o1.getValue());
                    if (cmp == 0) {
                        cmp = o2.getKey().length() - o1.getKey().length();
                    }
                    return cmp;
                }
            });
            str = list.get(0).getKey();
        }
        return str;
    }

}
