package com.wd.cloud.commons.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/6/13
 * @Description:
 */

@Data
@Accessors(chain = true)
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private Date gmtCreate;
    private Date gmtModified;
    private String email;
    private String nickname;
    private String realname;
    private String orgFlag;
    private String orgName;
    private Long departmentId;
    private String departmentName;
    private String phone;
    private String qq;
    private Integer sex;
    /**
     * 用户类型：1普通，2机构管理员，3后台管理员
     */
    private Integer userType;

    /**身份类型 1:学生，2：老师*/
    private Integer identityType;

    /**职工号/学号*/
    private String studentId;

    /**教育程度*/
    private Integer eduLevel;

    /**入学年份*/
    private String entranceYear;

    /** 证件照*/
    private String idPhoto;

    /** 头像*/
    private String headImg;

    /**登陆类型,1 normal，2qq，3wechat，4weibo*/
    private String loginType;

    private String qqOpenid;
    private String wechatOpenid;
    private String weiboOpenid;

    private String registerIp;
    private String lastLoginIp;
    private Integer loginCount;
    private boolean online;
    private boolean forbidden;
    /**
     * 是否是认证用户
     */
    private boolean validated;

    private OrgDTO org;

}
