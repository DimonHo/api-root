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

    List<DocFile> findByLiteratureIdOrderByReusingDescGmtModifiedDesc(Long literatureId);

    DocFile findByLiteratureIdAndReusingIsTrue(long literature);

    Optional<DocFile> findByLiteratureIdAndFileId(long literatureId, String fileId);

    List<DocFile> findByLiteratureIdAndBigDbFalse(Long literatureId);

    List<DocFile> findByLiteratureIdIn(List ids);

    Optional<DocFile> findByFileIdAndLiteratureId(String fileId , long literatureId);




}
