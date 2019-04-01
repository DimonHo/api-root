package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description: 机构联系人
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "org_linkman")
public class OrgLinkman extends AbstractEntity {

    @Column(name = "org_flag")
    private String orgFlag;
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


}
