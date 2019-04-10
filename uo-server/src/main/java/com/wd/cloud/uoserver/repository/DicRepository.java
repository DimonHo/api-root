package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.Dic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/23 15:22
 * @Description:
 */
public interface DicRepository extends JpaRepository<Dic, Long> {

    /**
     * 查询字典名
     *
     * @param tableName
     * @param columnName
     * @param value
     * @return
     */
    Dic findByTableNameAndColumnNameAndValue(String tableName, String columnName, Integer value);
}
