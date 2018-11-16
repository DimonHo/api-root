package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 前台展示数据模型
 */
@ApiModel(value = "返回给前台数据对象")
public class ViewDataModel {

    private Date tjDate;

    private QuotaModel quotaModels;

    public Date getTjDate() {
        return tjDate;
    }

    public ViewDataModel setTjDate(Date tjDate) {
        this.tjDate = tjDate;
        return this;
    }

    public QuotaModel getQuotaModels() {
        return quotaModels;
    }

    public ViewDataModel setQuotaModels(QuotaModel quotaModels) {
        this.quotaModels = quotaModels;
        return this;
    }
}
