package com.wd.cloud.docdelivery.model;

/**
 * @author He Zhigang
 * @date 2018/10/19
 * @Description:
 */
public class DefaultMailNotifyModel {
    /**
     * 邮件标题
     */
    private String mailTitle = "用户文献互助";
    /**
     * 机构名称
     */
    private String helperScname;
    /**
     * 求助用户
     */
    private String helperName;

    public String getMailTitle() {
        return mailTitle;
    }

    public DefaultMailNotifyModel setMailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
        return this;
    }

    public String getHelperScname() {
        return helperScname;
    }

    public DefaultMailNotifyModel setHelperScname(String helperScname) {
        this.helperScname = helperScname;
        return this;
    }

    public String getHelperName() {
        return helperName;
    }

    public DefaultMailNotifyModel setHelperName(String helperName) {
        this.helperName = helperName;
        return this;
    }
}
