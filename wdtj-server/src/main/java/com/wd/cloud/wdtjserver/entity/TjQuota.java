package com.wd.cloud.wdtjserver.entity;

import com.wd.cloud.wdtjserver.utils.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 日基数设置表 pv >= sc & vv >= uv
 * 1. 浏览量 >= 搜索量 和 访问次数 >= 访客数量
 * 2. 平均访问时长 = 总时长/访问次数
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tj_quota", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_flag", "pid"})
})
public class TjQuota extends AbstractEntity {

    /**
     * 机构ID
     */
    @Column(name = "org_flag")
    private String orgFlag;

    private String orgName;

    private int pvCount;

    private int scCount;

    private int dcCount;

    private int ddcCount;

    private int uvCount;

    private int vvCount;

    private Time avgTime = DateUtil.createTime(0);

    private String createUser;

    @Column(name = "pid")
    private Long pid;

    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否历史记录 0：否，1：是'")
    private boolean history;

}
