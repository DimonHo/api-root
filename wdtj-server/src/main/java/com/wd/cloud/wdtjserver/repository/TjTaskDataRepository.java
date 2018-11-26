package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjTaskDataRepository extends JpaRepository<TjTaskData, TjDataPk> {

    TjTaskData findByIdOrgId(long orgId);

    /**
     * 获取当前这一分钟的task数据
     * @return
     */
    @Query(value = "select * from tj_task_data where date_format(tj_date ,\"%Y-%m-%d %H:%i\") = now()", nativeQuery = true)
    List<TjTaskData> getByTjDateNow();

    /**
     * 获取指定分钟的task数据
     * @return
     */
    @Query(value = "select * from tj_task_data where date_format(tj_date ,\"%Y-%m-%d %H:%i\") = ?1", nativeQuery = true)
    List<TjTaskData> getByTjDate(Date date);

}
