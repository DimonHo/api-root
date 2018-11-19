package com.wd.cloud.wdtjserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description: 展示给用户的最终数据
 */
@Entity
@Table(name = "tj_view_data")
public class TjViewData extends AbstractTjDataEntity {

}
