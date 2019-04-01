package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 14:57
 * @Description: 用户权限
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permission",uniqueConstraints = {@UniqueConstraint(columnNames={"username", "type","value"})})
public class Permission extends AbstractEntity {

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
