package com.wd.cloud.bse.service.transfrom.merge;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.ArrayList;
import java.util.List;

public class ShouluMergeRule extends MergeRule<String> {

    @Override
    protected String name() {
        return "shouluMerge";
    }

    @Override
    protected List<String> merge(List<String> dataList, ResourceIndex<Document> resource) {
        List<String> list = resource.getShoulu();
        List<String> shoulu = new ArrayList<>();
        if (list != null) {
            for (String string : list) {
                if (StrUtil.isNotBlank(string)) {
                    shoulu.add(string);
                }
            }
        }
        return shoulu;
    }


}
