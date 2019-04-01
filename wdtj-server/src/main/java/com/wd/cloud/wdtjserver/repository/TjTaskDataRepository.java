package com.wd.cloud.wdtjserver.repository;

import com.wd.cloud.wdtjserver.entity.TjDataPk;
import com.wd.cloud.wdtjserver.entity.TjTaskData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjTaskDataRepository extends JpaRepository<TjTaskData, TjDataPk> {

    TjTaskData findByIdOrgFlag(String orgFlag);

    /**
     * 获取当前这一分钟的task数据
     *
     * @return
     */
    @Query(value = "select * from tj_task_data where date_format(tj_date ,\"%Y-%m-%d %H:%i\") = date_format(now() ,\"%Y-%m-%d %H:%i\")", nativeQuery = true)
    List<TjTaskData> getByTjDateNow();

    /**
     * 获取指定分钟的task数据
     *
     * @return
     */
    @Query(value = "select * from tj_task_data where date_format(tj_date ,\"%Y-%m-%d %H:%i\") = date_format(?1 ,\"%Y-%m-%d %H:%i\")", nativeQuery = true)
    List<TjTaskData> getByTjDate(String date);

}
