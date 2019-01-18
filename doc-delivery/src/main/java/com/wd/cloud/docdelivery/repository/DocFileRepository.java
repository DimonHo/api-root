package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.DocFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/5/27
 * @Description:
 */
public interface DocFileRepository extends JpaRepository<DocFile, Long> {

    @Query(value = "select * from doc_file where literature_id =?1 order by is_reusing desc,gmt_modified desc", nativeQuery = true)
    List<DocFile> findByLiteratureId(Long literatureId);

    DocFile findByLiteratureIdAndReusingIsTrue(long literature);

    Optional<DocFile> findByLiteratureIdAndFileId(long literatureId, String fileId);


    @Query(value = "select * from doc_file where literature_id = ?1", nativeQuery = true)
    List<DocFile> getResuingDoc(Long literatureId);

    List<DocFile> findByLiteratureIdIn(List ids);

    Optional<DocFile> findByFileIdAndLiteratureId(String fileId , long literatureId);




}
