package com.wd.cloud.bse.vo;

import java.io.Serializable;

public class RuleInfo implements Serializable {

    private static final long serialVersionUID = 3464609501805190518L;
    private String dbLinkUrl;
    private String docLinkPattern;
    private String bookLinkPattern;
    private String ruleName;
    private Integer ruleOrder;
    private Integer dbId;
    private String newDocLinkPattern;

    public RuleInfo() {
    }

    public String getDbLinkUrl() {
        return dbLinkUrl;
    }

    public void setDbLinkUrl(String dbLinkUrl) {
        this.dbLinkUrl = dbLinkUrl;
    }

    public String getDocLinkPattern() {
        return docLinkPattern;
    }

    public void setDocLinkPattern(String docLinkPattern) {
        this.docLinkPattern = docLinkPattern;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getRuleOrder() {
        return ruleOrder;
    }

    public void setRuleOrder(Integer ruleOrder) {
        this.ruleOrder = ruleOrder;
    }

    public String getBookLinkPattern() {
        return bookLinkPattern;
    }

    public void setBookLinkPattern(String bookLinkPattern) {
        this.bookLinkPattern = bookLinkPattern;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getNewDocLinkPattern() {
        return newDocLinkPattern;
    }

    public void setNewDocLinkPattern(String newDocLinkPattern) {
        this.newDocLinkPattern = newDocLinkPattern;
    }

}
