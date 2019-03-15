package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.CdbDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface OrgCdbService {

    Page<CdbDTO> findByOrgIdAndCollection(Pageable pageable, Long orgId, Boolean collection);

    Page<CdbDTO> findByOrgIdAndLocalUrlIsNotNull(Pageable pageable,Long orgId);

    Page<CdbDTO> findByNameAndUrl(Pageable pageable,String keyword);


    /**
     * 编辑馆藏资源
     * @param id
     * @param name
     * @param url
     * @param display
     */
    void updateOrgCdb(Long id, String name, String url, Boolean display);

    void insertOrgCdb(String name,String url,Long orgId,Boolean display);

    void deleteOrgCdb(Long id);

    void insertCdbUrl(String name ,String url ,Long orgId,String localUrl);


}
