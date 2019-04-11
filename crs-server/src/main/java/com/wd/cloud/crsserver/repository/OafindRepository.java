package com.wd.cloud.crsserver.repository;

import com.wd.cloud.crsserver.pojo.document.Oafind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.annotation.Resource;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/11 9:42
 * @Description:
 */
@Resource
public interface OafindRepository extends ElasticsearchRepository<Oafind, String> {

    Page<Oafind> findByDocumentType(String documentType, Pageable pageable);
}
