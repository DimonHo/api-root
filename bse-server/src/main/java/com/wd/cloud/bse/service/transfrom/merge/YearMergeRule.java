package com.wd.cloud.bse.service.transfrom.merge;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.List;

public class YearMergeRule extends MergeRule<String> {

    private static String spliter = ";";

    private static String replace = "#&#&#";

    @Override
    protected String name() {
        return "yearMerge";
    }

    @Override
    protected Integer merge(List<String> dataList, ResourceIndex<Document> resource) {
        return resource.getYear();
    }


}
