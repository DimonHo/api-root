package com.wd.cloud.docdelivery.entity;

import cn.hutool.crypto.SecureUtil;
import com.wd.cloud.commons.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description: 求助记录
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "help_record", uniqueConstraints = {@UniqueConstraint(columnNames = {"unid"})})
public class HelpRecord extends AbstractEntity {

    /**
     * 文献ID
     */
    private Long literatureId;

    /**
     * 求助的email地址
     */
    @Column(name = "helper_email")
    private String helperEmail;

    /**
     * 求助者用户名
     */
    private String helperName;

    /**
     * 求助用户的机构id
     */
    @Column(name = "helper_scid")
    private Long helperScid;

    /**
     * 求助用户的机构名称
     */
    private String helperScname;

    /**
     * 求助IP
     */
    @Column(name = "helper_ip")
    private String helperIp;
    /**
     * 求助渠道，1：QQ，2：SPIS，3：ZHY，4：CRS
     */
    @Column(name = "help_channel", columnDefinition = "tinyint default 0 COMMENT '求助渠道，1：QQ，2：SPIS，3：ZHY，4：CRS'")
    private int helpChannel;
    /**
     * 互助状态
     * 0：待应助，
     * 1：应助中（用户已认领，15分钟内上传文件），
     * 2: 待审核（用户已应助），
     * 3：求助第三方（第三方应助），
     * 4：应助成功（审核通过或管理员应助），
     * 5：应助失败（超过15天无结果）
     */
    @Column(name = "status", columnDefinition = "tinyint default 0 COMMENT '0：待应助， 1：应助中（用户已认领，15分钟内上传文件）， 2: 待审核（用户已应助）， 3：求助第三方（第三方应助）， 4：应助成功（审核通过或管理员应助）， 5：应助失败（超过15天无结果）'")
    private int status;

    /**
     * 是否成功发送邮件
     */
    @Column(name = "is_send", columnDefinition = "bit default 1 COMMENT '0：未发送邮件， 1：已成功发送邮件'")
    private boolean send;

    /**
     * 是否匿名
     */
    @Column(name = "is_anonymous", columnDefinition = "bit default 0 COMMENT '0：未匿名， 1：已匿名'")
    private boolean anonymous;

    private String unid;
    /**
     * 文献信息
     */
    @Column(name = "remark")
    private String remark;

    @PrePersist
    public void createUnid() {
        this.unid = SecureUtil.md5(this.helperEmail + this.literatureId + DateUtil.formatDate(this.gmtCreate));
    }

    @PreUpdate
    public void updateUnid() {
        this.unid = SecureUtil.md5(this.helperEmail + this.literatureId + DateUtil.formatDate(this.gmtCreate));
    }

}
