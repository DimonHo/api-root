package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author He Zhigang
 * @date 2018/11/8 0008
 * @Description: 历史统计指标模型
 */
@ApiModel(value = "历史统计指标模型")
public class HisQuotaModel extends QuotaModel {

    @ApiModelProperty(value = "开始时间")
    private String beginTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    public String getBeginTime() {
        return beginTime;
    }

    public HisQuotaModel setBeginTime(String beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public HisQuotaModel setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }
}
