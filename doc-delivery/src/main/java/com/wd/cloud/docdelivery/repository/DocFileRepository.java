package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/5/27
 * @Description:
 */
public interface DocFileRepository extends JpaRepository<DocFile, Long> {


    DocFile findByFileId(String fileId);

    List<DocFile> findByFileIdIsNull();

    DocFile findByLiteratureAndReusingIsTrue(Literature literature);

    DocFile findByLiteratureAndFileId(Literature literature, String fileId);

    @Query("from DocFile where literature = :literature and (auditStatus is null or auditStatus = 1)")
    List<DocFile> getResuingDoc(@Param("literature") Literature literature);

}
