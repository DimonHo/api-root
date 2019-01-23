package com.wd.cloud.bse.service.transfrom.merge;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.List;

public class SubjectsMergeRule extends MergeRule<String> {

    @Override
    protected String name() {
        return "subjectsMerge";
    }

    @Override
    protected String merge(List<String> dataList, ResourceIndex<Document> resource) {
        StringBuilder subjects = new StringBuilder();
        if (dataList != null) {
            for (String string : dataList) {
                if (StrUtil.isNotBlank(string)) {
                    subjects.append(";").append(string);
                }
            }
        }
        return subjects.toString().replaceFirst(";", "");
    }


}
