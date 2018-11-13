package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * @author He Zhigang
 * @date 2018/11/8 0008
 * @Description:
 */
@ApiModel(value = "历史统计指标对象")
public class HisQuotaModel extends QuotaModel {

    @ApiModelProperty(value = "开始时间")
    private Timestamp beginTime;
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public HisQuotaModel setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public HisQuotaModel setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        return this;
    }
}
