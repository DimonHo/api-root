package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 根据日基数算法每天生成数据
 */
@Entity
@Table(name = "tj_task_data")
public class TjTaskData extends AbstractTjDataEntity {

}
