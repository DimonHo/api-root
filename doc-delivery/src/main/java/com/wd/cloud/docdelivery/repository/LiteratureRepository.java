package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface LiteratureRepository extends JpaRepository<Literature, Long>, JpaSpecificationExecutor<Literature> {

    boolean existsByUnid(String unid);

    /**
     * 根据unid唯一码查询
     *
     * @param unid
     * @return
     */
    Literature findByUnid(String unid);

    List<Literature> findByUnidIsNull();

    @Query(value = "select doc_href,doc_title from literature where unid is null group by doc_href,doc_title",nativeQuery = true)
    List<Map<String,String>> findByUnidIsNullGroupBy();

    /**
     * 根据文献标题查询文献元数据
     *
     * @param docTitle
     * @return
     */
    Literature findByDocTitle(String docTitle);


    /**
     * 根据文献标题和文献连接查询元数据
     *
     * @param docTitle
     * @param docHref
     * @return
     */
    //Literature findByDocTitleAndDocHref(String docTitle, String docHref);

    List<Literature> findByDocTitleAndDocHref(String docTitle, String docHref);

    List<Literature> findByDocHrefAndDocTitle(String docHref, String docTitle);

    List<Literature> findByDocHrefIsNullAndDocTitle(String docTitle);

    List<Literature> deleteByIdIn(List ids);

}
