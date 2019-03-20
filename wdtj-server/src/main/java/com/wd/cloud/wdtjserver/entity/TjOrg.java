package com.wd.cloud.wdtjserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 机构设置表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tj_org", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_falg", "pid"})
})
public class TjOrg extends AbstractEntity {
    /**
     * 机构ID
     */
    @Column(name = "org_flag")
    private String orgFlag;
    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 是否显示浏览量
     */
    private boolean showPv;

    /**
     * 是否显示访客数量
     */
    private boolean showUv;

    /**
     * 是否显示访问次数
     */
    private boolean showVv;

    /**
     * 是否显示下载量
     */
    private boolean showDc;
    /**
     * 是否显示搜索量
     */
    private boolean showSc;
    /**
     * 是否显示文献传递量
     */
    private boolean showDdc;
    /**
     * 是否显示访问量 = 总访问时长/访问次数 (avgTime = sum(visitTime)/vvCount)
     */
    private boolean showAvgTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 历史数据Id
     */
    private Long pid;

    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否是历史记录 0：否，1：是'")
    private boolean history;

    /**
     * 是否禁用
     */
    @Column(name = "is_forbade", columnDefinition = "bit default 0 COMMENT '是否被禁用 0：否，1：是'")
    private boolean forbade;

}
