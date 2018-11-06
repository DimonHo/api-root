package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Date;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: spis访问的真实数据
 */
@Entity
@Table(name = "tj_spis_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id","tj_date"})
})
public class TjSpisData {

    /**
     * 机构ID
     */
    private Long orgId;

    private int pvCount;
    private int scCount;
    private int dcCount;
    private int ddcCount;
    private Time avgTime;
    /**
     * 时间，精确到分钟
     */
    private Date tjDate;
}
