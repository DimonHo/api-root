package com.wd.cloud.docdelivery.model;

/**
 * @author He Zhigang
 * @date 2018/12/11
 * @Description: 统计分析返回对象
 */
public class AnalysisModel {

    private String orgName;
    private String orgId;
    /**
     * 求助总数量
     */
    private int sumCount;
    /**
     * 求助人数
     */
    private int helperCount;
    /**
     * 成功数量
     */
    private int successCount;
    /**
     * 失败数量
     */
    private int failedCount;
    /**
     * 待应助，待审核，待上传，求助第三方都算otherCount
     */
    private int otherCount;

    public String getOrgName() {
        return orgName;
    }

    public AnalysisModel setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public String getOrgId() {
        return orgId;
    }

    public AnalysisModel setOrgId(String orgId) {
        this.orgId = orgId;
        return this;
    }

    public int getSumCount() {
        return sumCount;
    }

    public AnalysisModel setSumCount(int sumCount) {
        this.sumCount = sumCount;
        return this;
    }

    public int getHelperCount() {
        return helperCount;
    }

    public AnalysisModel setHelperCount(int helperCount) {
        this.helperCount = helperCount;
        return this;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public AnalysisModel setSuccessCount(int successCount) {
        this.successCount = successCount;
        return this;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public AnalysisModel setFailedCount(int failedCount) {
        this.failedCount = failedCount;
        return this;
    }

    public int getOtherCount() {
        return otherCount;
    }

    public AnalysisModel setOtherCount(int otherCount) {
        this.otherCount = otherCount;
        return this;
    }
}
