package com.wd.cloud.docdelivery.entity;

import cn.hutool.core.util.ReflectUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description: 求助记录
 */
@Entity
@Table(name = "help_record")
public class HelpRecord extends AbstractEntity  {

    /**
     * 文献ID
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Literature literature;

    @OneToMany(mappedBy = "helpRecord")
    @OrderBy(value = "gmt_create desc")
    private Set<GiveRecord> giveRecords;

    /**
     * 求助的email地址
     */
    @Email
    @Column(name = "helper_email")
    private String helperEmail;

    /**
     * 求助用户ID
     */
    @Column(name = "helper_id")
    private Long helperId;

    /**
     * 求助者名称
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

    /**
     * 文献信息
     */
    @Column(name = "remake")
    private String remake;

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }

    public Literature getLiterature() {
        return literature;
    }

    public void setLiterature(Literature literature) {
        this.literature = literature;
    }

    public String getHelperEmail() {
        return helperEmail;
    }

    public void setHelperEmail(String helperEmail) {
        this.helperEmail = helperEmail;
    }

    public Long getHelperId() {
        return helperId;
    }

    public void setHelperId(Long helperId) {
        this.helperId = helperId;
    }

    public Long getHelperScid() {
        return helperScid;
    }

    public void setHelperScid(Long helperScid) {
        this.helperScid = helperScid;
    }

    public String getHelperIp() {
        return helperIp;
    }

    public void setHelperIp(String helperIp) {
        this.helperIp = helperIp;
    }

    public Integer getHelpChannel() {
        return helpChannel;
    }

    public void setHelpChannel(Integer helpChannel) {
        this.helpChannel = helpChannel;
    }

    public void setHelpChannel(int helpChannel) {
        this.helpChannel = helpChannel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHelperScname() {
        return helperScname;
    }

    public void setHelperScname(String helperScname) {
        this.helperScname = helperScname;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public Set<GiveRecord> getGiveRecords() {
        return giveRecords;
    }

    public void setGiveRecords(Set<GiveRecord> giveRecords) {
        this.giveRecords = giveRecords;
    }

    public boolean isSend() {
        return send;
    }

    public HelpRecord setSend(boolean send) {
        this.send = send;
        return this;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public HelpRecord setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("literature", literature)
                .append("giveRecords", giveRecords)
                .append("helperEmail", helperEmail)
                .append("helperId", helperId)
                .append("helperName", helperName)
                .append("helperScid", helperScid)
                .append("helperScname", helperScname)
                .append("helperIp", helperIp)
                .append("helpChannel", helpChannel)
                .append("status", status)
                .append("send",send)
                .append("anonymous",anonymous)
                .append("id", id)
                .append("gmtModified", gmtModified)
                .append("gmtCreate", gmtCreate)
                .toString();
    }

    public HelpRecord filterByNotIn(String fieldName, List<Object> values) {
        List<GiveRecord> giveRecords = new ArrayList<>();
        this.getGiveRecords().stream()
                .filter(g -> values.contains(ReflectUtil.getFieldValue(g, fieldName)))
                .forEach(giveRecords::add);
        this.getGiveRecords().removeAll(giveRecords);
        return this;
    }

    public HelpRecord filterByNotEq(String fieldName, Object value) {
        List<GiveRecord> giveRecords = new ArrayList<>();
        this.getGiveRecords().stream()
                .filter(g -> value.equals(ReflectUtil.getFieldValue(g, fieldName)))
                .forEach(giveRecords::add);
        this.getGiveRecords().removeAll(giveRecords);
        return this;
    }

    public HelpRecord filterByIn(String fieldName, List<Object> values) {
        List<GiveRecord> giveRecords = new ArrayList<>();
        this.getGiveRecords().stream()
                .filter(g -> !values.contains(ReflectUtil.getFieldValue(g, fieldName)))
                .forEach(giveRecords::add);
        this.getGiveRecords().removeAll(giveRecords);
        return this;
    }

    public HelpRecord filterByEq(String fieldName, Object value) {
        List<GiveRecord> giveRecords = new ArrayList<>();
        this.getGiveRecords().stream()
                .filter(g -> !value.equals(ReflectUtil.getFieldValue(g, fieldName)))
                .forEach(giveRecords::add);
        this.getGiveRecords().removeAll(giveRecords);
        return this;
    }
}
