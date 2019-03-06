package com.wd.cloud.uoserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author He Zhigang
 * @date 2019/1/15
 * @Description: 用户表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user")
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    private String password;

    private String nickname;

    private String email;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "org_name")
    private String orgName;
    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像
     */
    @Column(name = "head_img")
    public String headImg;

    /**
     * 用户类型
     */
    @Column(name = "user_type")
    public Integer userType;

    /**
     * 注册IP
     */
    @Column(name = "register_ip")
    private String registerIp;


    /**
     * qqID
     */
    @Column(name = "qq_qpenid")
    private String qqOpenid;
    /**
     * 微信ID
     */
    @Column(name = "wechat_openid")
    private String wechatOpenid;
    /**
     * 微博ID
     */
    @Column(name = "weibo_openid")
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
    @Column(name = "department_id")
    private String departmentId;

    /**教育程度*/
    private Integer education;

    /**性别*/
    private Short sex;

    /**入学年份*/
    @Column(name = "entrance_time")
    private String entranceTime;

    /**登录权限*/
    private Integer permission = 0;

    /**是否在线*/
    @Column(name = "is_online",columnDefinition = "tinyint(1) default 0 COMMENT '0:在线，1：离线'")
    private boolean isOnline;

    private Integer forbidden;
}
