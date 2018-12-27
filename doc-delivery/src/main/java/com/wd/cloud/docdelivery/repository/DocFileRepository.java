package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/5/27
 * @Description:
 */
public interface DocFileRepository extends JpaRepository<DocFile, Long> {


    DocFile findByFileId(String fileId);

    List<DocFile> findByFileIdIsNull();

    @Query(value = "select * from doc_file where literature_id =?1 order by is_reusing desc,gmt_modified desc", nativeQuery = true)
    List<DocFile> findByLiteratureId(Long literatureId);

    DocFile findByLiteratureAndReusingIsTrue(Literature literature);

    Optional<DocFile> findByLiteratureAndFileId(Literature literature, String fileId);

    //DocFile findByLiteratureAndFileId(Literature literature, String fileId);

    @Query(value = "select * from doc_file where literature_id = ?1 and (audit_status is null or audit_status = 1)", nativeQuery = true)
    List<DocFile> getResuingDoc(@Param("literature") Long literatureId);

}
