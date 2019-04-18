package com.wd.cloud.crsserver.repository;

import com.wd.cloud.crsserver.pojo.entity.WdSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/12 11:42
 * @Description:
 */
public interface WdSubjectRepository extends JpaRepository<WdSubject, Long> {
    /**
     * 查询索引学科
     * @param index
     * @return
     */
    List<WdSubject> findByIndexType(String index);

    /**
     * 查询多个索引学科
     * @param indexes
     * @return
     */
    List<WdSubject> findByIndexTypeIn(List<String> indexes);
}
