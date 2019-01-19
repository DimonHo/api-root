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
public class UserInfo extends AbstractEntity{


    @Column(name = "user_id")
    private Long userId;
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
    private Boolean validated;
}
