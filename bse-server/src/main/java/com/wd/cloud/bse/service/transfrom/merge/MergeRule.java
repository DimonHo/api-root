package com.wd.cloud.bse.service.transfrom.merge;

import com.wd.cloud.bse.data.Document;
import com.wd.cloud.bse.data.ResourceIndex;

import java.util.List;
import java.util.ServiceLoader;


public abstract class MergeRule<T> {

    @SuppressWarnings("rawtypes")
    private static ServiceLoader<MergeRule> loader = ServiceLoader.load(MergeRule.class);

    @SuppressWarnings("rawtypes")
    public static MergeRule getRule(String name) {
        MergeRule instance = null;
        for (MergeRule rule : loader) {
            if (rule.name() == name) {
                try {
                    instance = rule.getClass().newInstance();
                } catch (Exception e) {
                }
            }
        }
        return instance;
    }

    protected abstract String name();

    //	protected abstract Object merge(List<T> dataList, Map<String, String> parameters);
    protected abstract Object merge(List<T> dataList, ResourceIndex<Document> resource);


}
