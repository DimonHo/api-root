package com.wd.cloud.orgserver.entity;

import javax.persistence.ManyToOne;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构联系人信息
 */
public class OrgContacts extends AbstractEntity{

    /**
     * 联系人名称
     */
    private String name;
    /**
     * 联系人邮箱
     */
    private String email;
    /**
     * 联系人电话
     */
    private String phone;

    @ManyToOne
    private OrgInfo orgInfo;

}
