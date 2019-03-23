package com.wd.cloud.uoserver.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/15 11:43
 * @Description: 机构联系人
 */
@Data
@Accessors(chain = true)
public class LinkmanDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
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
