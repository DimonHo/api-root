package com.wd.cloud.docdelivery.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@Getter
@Setter
@Accessors(chain = true)
public class TjDTO {

    /**
     * 求助总量
     */
    private Long totalForHlep;

    /**
     * 今日求助总量
     */
    private Long todayTotalForHelp;

    /**
     * 求助成功总量
     */
    private Long successTotal;

    /**
     * 求助成功率
     */
    private String successRate;
}
