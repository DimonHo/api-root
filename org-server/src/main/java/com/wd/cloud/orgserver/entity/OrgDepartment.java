package com.wd.cloud.orgserver.entity;

import javax.persistence.ManyToOne;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 院系
 */
public class OrgDepartment {

    /**
     * 所属上级院系
     */
    private Long pid;

    /**
     * 院系名称
     */
    private String name;

    @ManyToOne
    private OrgInfo orgInfo;
}
