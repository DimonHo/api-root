package com.wd.cloud.bse.service.transfrom.merge;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryMergeRule extends MergeRule<String> {

    @Override
    protected String name() {
        return "categoryMerge";
    }

    @Override
    protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list;

        String category = "";
        for (String string : dataList) {
            String[] s = string.split(":");
            if (s.length >= 2) {
                if (map.containsKey(s[0])) {
                    list = map.get(s[0]);
                } else {
                    list = new ArrayList<String>();
                }
                list.add(s[1]);
                map.put(s[0], list);
            }
//			category = category + ";" + string;
        }
        for (String key : map.keySet()) {
            list = map.get(key);
            String data = key + ":";
            for (String cat : list) {
                data = data + cat + ";";
            }
            category = category + ";" + data;
        }
        return category.replaceFirst(";", "");
    }


}
