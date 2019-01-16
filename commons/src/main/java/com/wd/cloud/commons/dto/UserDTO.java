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
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private Boolean forbidden;
    private Date loginTime;
    private String lastLoginIp;
    private String registerIp;
    private Date registerTime;
    private String phone;
    private String qq;
    private Integer sex;
    private String qqOpenid;
    private String wechatOpenid;
    private String weiboOpenid;
    private boolean verify;
    private OrgDTO org;

}
