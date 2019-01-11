package com.wd.cloud.authserver.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2019/1/11
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserInfoDTO {

    private String username;
    private String email;
    private String orgName;
    private String orgId;

    private String nickname;
    private String phone;

    private String qq;
    private Integer sex;

    private String lastLoginIp;
    private Date loginTime;

    private String registerIp;
    private Date registerTime;

    private String qqOpenid;

    private String wechatOpenid;

    private String weiboOpenid;
}
