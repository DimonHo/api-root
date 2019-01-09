package com.wd.cloud.docdelivery.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author He Zhigang
 * @date 2018/5/16
 * @Description:
 */
@ApiModel(value = "文献求助post对象")
public class HelpRequestModel {

    /**
     * 求助用户ID
     */
    @ApiModelProperty(value = "求助用户ID", example = "1000")
    private Long helperId;

    /**
     * 求助用户名称
     */
    @ApiModelProperty(value = "求助用户名称", example = "dimon")
    private String helperName;

    /**
     * 求助渠道
     */
    @ApiModelProperty(value = "求助渠道", example = "1")
    @NotNull
    private Integer helpChannel;

    /**
     * 求助用户所属机构id
     */
    @ApiModelProperty(value = "求助者所属机构ID", example = "60")
    private Long helperScid;

    /**
     * 求助用户所属机构名
     */
    @ApiModelProperty(value = "求助者所属机构名称", example = "中南大学")
    private String helperScname;

    /**
     * 求助文件标题
     */
    @ApiModelProperty(value = "求助文献标题", example = "关于xxx可行性研究")
    @NotBlank
    private String docTitle;

    /**
     * 求助文献连接
     */
    @ApiModelProperty(value = "求助文献链接", example = "http://www.xxx.com")
    private String docHref;

    /**
     * 求助用户邮箱
     */
    @ApiModelProperty(value = "求助用户邮箱", example = "hezhigang@qq.com")
    @NotBlank
    private String helperEmail;

    @ApiModelProperty(value = "补充信息", example = "doi:01923959101,xxx:102030104")
    private String remark;

    @ApiModelProperty(value = "是否匿名,默认false", example = "false")
    private boolean anonymous = false;

    public Long getHelperId() {
        return helperId;
    }

    public void setHelperId(Long helperId) {
        this.helperId = helperId;
    }

    public Integer getHelpChannel() {
        return helpChannel;
    }

    public void setHelpChannel(Integer helpChannel) {
        this.helpChannel = helpChannel;
    }

    public Long getHelperScid() {
        return helperScid;
    }

    public void setHelperScid(Long helperScid) {
        this.helperScid = helperScid;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocHref() {
        return docHref;
    }

    public void setDocHref(String docHref) {
        this.docHref = docHref;
    }

    public String getHelperEmail() {
        return helperEmail;
    }

    public void setHelperEmail(String helperEmail) {
        this.helperEmail = helperEmail;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperScname() {
        return helperScname;
    }

    public void setHelperScname(String helperScname) {
        this.helperScname = helperScname;
    }

    public String getRemark() {
        return remark;
    }

    public HelpRequestModel setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public HelpRequestModel setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }
}
