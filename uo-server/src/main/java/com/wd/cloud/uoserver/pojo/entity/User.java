package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

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

    @Id
    @GeneratedValue
    private String username;

    private String email;

    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realname;


    @Column(name = "org_flag")
    private String orgFlag;

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
    @Column(name = "user_type",columnDefinition = "tinyint(1) default 1 COMMENT '1:普通用户，1：机构管理员用户，2，后台操作员用户，9：超级管理员用户'")
    public Integer userType;

    /**
     * 注册IP
     */
    @Column(name = "register_ip")
    private String registerIp;

    /**
     * qqID
     */
    @Column(name = "qq_openid")
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
     * 认证状态
     */
    @Column(name = "valid_status", columnDefinition = "tinyint(1) default 0 COMMENT '0:未认证，1：待认证，2：已认证'")
    private Integer validStatus;

    /**院系*/
    private Long departmentId;

    /**身份类型 1:学生，2：老师*/
    private Integer identityType;

    /**职工号/学号*/
    @Column(name = "student_id")
    private String studentId;

    /**教育程度*/
    private Integer eduLevel;

    /**性别*/
    private Short sex;

    /**入学年份*/
    @Column(name = "entrance_Year")
    private String entranceYear;

    /**是否在线*/
    @Column(name = "is_online",columnDefinition = "tinyint(1) default 0 COMMENT '0:离线，1：在线'")
    private boolean online;

    /**账号是否被禁用*/
    @Column(name = "is_forbidden",columnDefinition = "tinyint(1) default 0 COMMENT '0:未禁用，1：禁用'")
    private boolean forbidden;

}
