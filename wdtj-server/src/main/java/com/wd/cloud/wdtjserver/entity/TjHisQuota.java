package com.wd.cloud.wdtjserver.entity;

import com.wd.cloud.wdtjserver.utils.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tj_his_quota")
public class TjHisQuota extends AbstractEntity {

    /**
     * 机构ID
     */
    @Column(name = "org_name")
    private String orgFlag;

    private String orgName;

    private int pvCount;

    private int scCount;

    private int dcCount;

    private int ddcCount;

    private int uvCount;

    private int vvCount;

    private Time avgTime = DateUtil.createTime(0);

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String createUser;
    /**
     * 是否上锁，如果已上锁，则不可覆盖
     */
    @Column(name = "is_locked", columnDefinition = "bit default 0 COMMENT '是否已经被锁定无法修改'")
    private boolean locked;

    /**
     * 是否是历史的记录
     */
    @Column(name = "is_history", columnDefinition = "bit default 0 COMMENT '是否是历史记录（作废），0:否，1：是'")
    private boolean history;

    /**
     * 是否已生成过
     */
    @Column(name = "build_state", columnDefinition = "tinyint default 0 COMMENT '是否已经生成过数据，0：否，1：是, 2:生成中。。。'")
    private int buildState;
}
