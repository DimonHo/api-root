package com.wd.cloud.uoserver.service;

import com.wd.cloud.uoserver.pojo.dto.OrgCdbDTO;
import com.wd.cloud.uoserver.pojo.vo.OrgCdbVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrgCdbService {

    /**
     * 查询机构馆藏数据库
     *
     * @param pageable
     * @param orgFlag
     * @param collection 是否是馆藏资源
     * @return
     * @Param local 是否有localUrl
     */
    Page<OrgCdbDTO> findOrgCdbs(String orgFlag, Boolean collection, Boolean local, String keyword, Pageable pageable);

    /**
     * 添加，修改，删除
     *
     * @param orgFlag
     * @return
     */
    void saveOrgCdb(String orgFlag, OrgCdbVO orgCdbVO);


    /**
     * 批量添加，修改，删除
     *
     * @param orgFlag
     * @param orgCdbVOS
     * @return
     */
    void saveOrgCdb(String orgFlag, List<OrgCdbVO> orgCdbVOS);
}
