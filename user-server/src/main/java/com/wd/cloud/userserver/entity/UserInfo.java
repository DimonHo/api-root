package com.wd.cloud.userserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
     * 证件照
     */
    @Column(name = "id_photo")
    private String idPhoto;

    /**
     * 是否已认证
     */
    @Column(name = "is_validated", columnDefinition = "tinyint(1) default 0 COMMENT '0:未认证，1：已认证'")
    private boolean validated;
}
