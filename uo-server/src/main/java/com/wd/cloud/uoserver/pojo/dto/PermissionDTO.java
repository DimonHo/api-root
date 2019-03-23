package com.wd.cloud.uoserver.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/22 14:08
 * @Description:
 */
@Data
@Accessors(chain = true)
public class PermissionDTO {
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 权限类型 1：校外访问权限，其他权限待添加
     */
    private Integer type;
    /**
     * 权限值  校外权限值：1:6个月校外权限，2：永久校外权限
     */
    private Integer value;
    /**
     * 生效时间
     */
    private Date effDate;
    /**
     * 失效时间
     */
    private Date expDate;
}
