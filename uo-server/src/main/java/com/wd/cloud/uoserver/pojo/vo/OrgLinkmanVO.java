package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 21:38
 * @Description: 机构联系人
 */
@Data
@Accessors(chain = true)
public class OrgLinkmanVO {
    Long id;
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

    Boolean del;
}
