package com.wd.cloud.wdtjserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/8 0008
 * @Description: 历史统计指标模型
 */
@ApiModel(value = "历史统计指标模型")
public class HisQuotaModel extends QuotaModel {

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间")
    private Date beginTime;
    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    public Date getBeginTime() {
        return beginTime;
    }

    public HisQuotaModel setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public HisQuotaModel setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }
}
