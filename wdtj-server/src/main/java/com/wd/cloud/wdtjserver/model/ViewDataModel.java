package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 前台展示数据模型
 */
@ApiModel(value = "返回给前台数据对象")
public class ViewDataModel {
    /**
     * ["2018","2019","2020",...]
     * ["2018-01","2018-02","2018-03",...]
     * ["2018-01-01","2018-01-02","2018-01-03",...]
     * ["2018-01-01 00:00:00","2018-01-02  01:00:00","2018-01-03 02:00:00",...]
     */
    private List<String> lenged;

    private List<QuotaModel> quotaModels;

    public List<String> getLenged() {
        return lenged;
    }

    public ViewDataModel setLenged(List<String> lenged) {
        this.lenged = lenged;
        return this;
    }

    public List<QuotaModel> getQuotaModels() {
        return quotaModels;
    }

    public ViewDataModel setQuotaModels(List<QuotaModel> quotaModels) {
        this.quotaModels = quotaModels;
        return this;
    }
}
