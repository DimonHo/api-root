package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Date;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 展示给用户的最终数据
 */
@Entity
@Table(name = "tj_view_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id","tj_date"})
})
public class TjViewData {

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
     * 时间
     */
    private Date tjDate;
}
