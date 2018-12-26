package com.wd.cloud.docdelivery.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author He Zhigang
 * @date 2018/12/25
 * @Description:
 */
@Accessors(chain = true)
@Getter
@Setter
public class MyTjDTO {

    private Long userId;

    private String userName;

    private String email;
    /**
     * 可求助总数量
     */
    private Long total;

    /**
     * 每日可求助总数量
     */
    private Long todayTotal;

    /**
     * 总剩余量
     */
    private Long restTotal;

    /**
     * 今日剩余求助数量
     */
    private Long todayRestTotal;

    /**
     * 我的应助数量
     */
    private Long giveCount;

    /**
     * 我的已求助总数量
     */
    private Long helpCount;

    /**
     * 我的今日已求助总数量
     */
    private Long todayHelpCount;
    /**
     * 我求助成功的数量
     */
    private Long successHelpCount;

    /**
     * 我应助成功的数量
     */
    private Long successGiveCount;


}
