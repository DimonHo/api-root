package com.wd.cloud.userserver.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/3/1
 * @Description:
 */
@Data
@Accessors(chain = true)
public class RegisterModel {
    @NotNull
    private String username;

    private String nickname;

    private String email;

    private Long orgId;

    private String orgName;
    /**
     * 手机号码
     */
    private String phone;
    private String registerIp;
    private Date registerTime;

    private String qqOpenid;
    private String wechatOpenid;
    private String weiboOpenid;
}
