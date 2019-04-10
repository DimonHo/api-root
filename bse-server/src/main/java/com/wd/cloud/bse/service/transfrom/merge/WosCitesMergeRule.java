package com.wd.cloud.bse.service.transfrom.merge;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.List;

public class WosCitesMergeRule extends MergeRule<String> {

    @Override
    protected String name() {
        return "wosCitesMerge";
    }

    @Override
    protected Integer merge(List<String> dataList, ResourceIndex<Document> resource) {
        return resource.getWosCites();
    }


}
