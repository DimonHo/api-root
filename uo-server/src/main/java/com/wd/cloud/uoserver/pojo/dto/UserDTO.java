package com.wd.cloud.uoserver.pojo.dto;

import com.wd.cloud.uoserver.pojo.entity.Permission;
import com.wd.cloud.uoserver.pojo.entity.UserMsg;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String password;
    private Date gmtCreate;
    private Date gmtModified;
    private String email;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 真实姓名
     */
    private String realname;
    private String orgFlag;
    private String orgName;
    private Long orgDeptId;
    private String orgDeptName;
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
    /**
     * 登陆次数
     */
    private Integer loginCount;
    /**
     * 是否在线？0：离线，1：在线
     */
    private Boolean online;

    /**
     * 是否禁用？ 0：未禁用，1：已禁用
     */
    private Boolean forbidden;
    /**
     * 认证狀態 0:未认证，1：待认证，2：已认证
     */
    private Integer validStatus;

    private String handlerName;

    /**
     * 是否有校外访问权限
     */
    private List<Permission> permissions = new ArrayList<>();

    /**
     * 用户消息列表
     */
    private Page<UserMsg> msgs;

    private OrgDTO org;

}
