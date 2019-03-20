package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.OrgCdbDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface OrgCdbService {

    Page<OrgCdbDTO> findByOrgIdAndCollection(Pageable pageable, String orgFlag, Boolean collection);

    Page<OrgCdbDTO> findByOrgIdAndLocalUrlIsNotNull(Pageable pageable, String orgFlag);

    Page<OrgCdbDTO> findByNameAndUrl(Pageable pageable, String keyword);


    /**
     * 编辑馆藏资源
     * @param id
     * @param name
     * @param url
     * @param display
     */
    void updateOrgCdb(Long id, String name, String url, Boolean display);

    void insertOrgCdb(String name,String url,String orgFlag,Boolean display);

    void deleteOrgCdb(Long id);

    void insertCdbUrl(String name ,String url ,String orgFlag,String localUrl);


}
