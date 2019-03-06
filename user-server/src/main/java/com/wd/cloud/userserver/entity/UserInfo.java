package com.wd.cloud.userserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description:
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_info")
public class UserInfo extends AbstractEntity {

    @Column(unique = true)
    private String username;

    private String nickname;

    private String email;

    private Long orgId;

    private String orgName;
    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像
     */
    public String photo;

    /**
     * 用户类型
     */
    public Integer userType;
    private String registerIp;

    private String qqOpenid;
    private String wechatOpenid;
    private String weiboOpenid;
    /**
     * 证件照
     */
    @Column(name = "id_photo")
    private String idPhoto;

    /**
     * 是否已认证
     */
    @Column(name = "is_validated", columnDefinition = "tinyint(1) default 0 COMMENT '0:未认证，1：已认证'")
    private boolean validated;


    /**院系*/
    private String department;

    /**身份*/
    private Integer identity;

    /**职工号/学号*/
    private String departmentId;

    /**教育程度*/
    private Integer education;

    /**性别*/
    private Short sex;

    /**入学年份*/
    private String entranceTime;

    /**登录权限*/
    private Integer permission = 0;

    /**是否在线*/
    @Column(name = "is_online",columnDefinition = "tinyint(1) default 0 COMMENT '0:在线，1：离线'")
    private boolean isOnline;

    private Integer forbidden;

    private String pwd;
    






}
