package com.wd.cloud.uoserver.pojo.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/18 18:15
 * @Description:
 */
@Data
@Accessors(chain = true)
@Immutable
@Entity
@Table(name = "v_user")
public class VUser extends AbstractEntity{

    @Id
    private String username;

    @Column(name = "password")
    private String password;

    /**
     * 昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 真实姓名
     */
    @Column(name = "realname")
    private String realname;

    private String email;

    @Column(name = "org_flag")
    private String orgFlag;

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
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "department_name")
    private String departmentName;

    /**身份类型 1:学生，2：老师*/
    @Column(name = "identity_type")
    private Integer identityType;

    /**职工号/学号*/
    @Column(name = "student_id")
    private String studentId;

    /**教育程度*/
    @Column(name = "edu_level")
    private Integer eduLevel;

    /**性别*/
    private Short sex;

    /**入学年份*/
    @Column(name = "entrance_year")
    private String entranceYear;

    /**是否在线*/
    @Column(name = "is_online",columnDefinition = "tinyint(1) default 0 COMMENT '0:离线，1：在线'")
    private boolean online;

    /**账号是否被禁用*/
    @Column(name = "is_forbidden",columnDefinition = "tinyint(1) default 0 COMMENT '0:未禁用，1：禁用'")
    private boolean forbidden;

}
