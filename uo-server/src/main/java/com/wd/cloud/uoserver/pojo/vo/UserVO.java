package com.wd.cloud.uoserver.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserVO {
    @NotNull
    @Length(min = 6,max = 16)
    private String username;

    @Length(min = 6,max = 16)
    private String password;

    private String nickname;

    private String realname;

    @Email
    private String email;

    private String orgFlag;

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
