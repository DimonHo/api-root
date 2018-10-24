package com.wd.cloud.docdelivery.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.mail.Mail;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
public class MailModel extends Mail {

    /**
     * 有效期毫秒数
     */
    private long exp = 1000 * 60 * 60 * 24 * 15;

    /**
     * 有效期
     */
    private String expStr;

    /**
     * 内容模板文件
     */
    private String templateFile;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 渠道官网
     */
    private String channelUrl;

    private DefaultMailSuccessModel successModel;

    private DefaultMailFailedModel failedModel;

    private DefaultMailThirdModel thirdModel;

    private DefaultMailNotifyModel notifyModel;

    public long getExp() {
        return exp;
    }

    public MailModel setExp(long exp) {
        this.exp = exp;
        return this;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public MailModel setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public MailModel setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public MailModel setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
        return this;
    }

    public DefaultMailSuccessModel getSuccessModel() {
        return successModel;
    }

    public MailModel setSuccessModel(DefaultMailSuccessModel successModel) {
        this.successModel = successModel;
        return this;
    }

    public DefaultMailFailedModel getFailedModel() {
        return failedModel;
    }

    public MailModel setFailedModel(DefaultMailFailedModel failedModel) {
        this.failedModel = failedModel;
        return this;
    }

    public DefaultMailThirdModel getThirdModel() {
        return thirdModel;
    }

    public MailModel setThirdModel(DefaultMailThirdModel thirdModel) {
        this.thirdModel = thirdModel;
        return this;
    }

    public DefaultMailNotifyModel getNotifyModel() {
        return notifyModel;
    }

    public MailModel setNotifyModel(DefaultMailNotifyModel notifyModel) {
        this.notifyModel = notifyModel;
        return this;
    }

    public String getExpStr() {
        return DateUtil.date(exp).toString("yyyy-MM-dd HH:mm:ss");
    }

    public MailModel setExpStr(String expStr) {
        this.expStr = expStr;
        return this;
    }
}
