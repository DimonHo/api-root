package com.wd.cloud.uoserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_user_info")
public class AuditUserInfo extends AbstractEntity {

    private String username;

    /**
     * 证件照
     */
    @Column(name = "id_photo")
    private String idPhoto;

    /**
     * 状态
     */

    private Integer status;

    /**
     * 真实姓名
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 操作人
     */
    @Column(name = "handler_name")
    private String handlerName;

    /**
     * 失败原因
     */
    private String remark;

    /**
     * 学校
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 院系
     */
    private String department;

    /**
     * 身份
     */
    private Integer identity;

    /**
     * 职工号/学号
     */
    @Column(name = "department_id")
    private String departmentId;

    /**
     * 教育程度
     */
    private Integer education;

    /**
     * 性别
     */
    private Short sex;

    /**
     * 入学年份
     */
    @Column(name = "entrance_time")
    private String entranceTime;

    /**
     * 登录权限
     */
    @Column(columnDefinition = "tinyint(1) default 0 COMMENT '0:无校外访问权限，1：6个月校外权限，2:永久校外权限'")
    private Integer permission;

    @Column(name = "handle_time")
    private Date handleTime;
}
