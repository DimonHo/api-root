package com.wd.cloud.wdtjserver.model;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Access;

/**
 * @author He Zhigang
 * @date 2018/11/19
 * @Description: 指标总量
 */
@Data
@Accessors(chain = true)
public class TotalModel {

    private String orgFlag;

    private String orgName;

    private DateTime date;

    private int pvTotal;

    private int scTotal;

    private int dcTotal;

    private int ddcTotal;

    private long visitTimeTotal;

    private int uvTotal;

    private int vvTotal;


}
