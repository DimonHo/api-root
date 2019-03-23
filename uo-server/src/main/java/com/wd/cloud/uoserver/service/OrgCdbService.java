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
     * @param type 资源类型 1馆藏，2筛选
     * @return
     * @Param local 是否有localUrl
     */
    Page<OrgCdbDTO> findOrgCdbs(String orgFlag, Integer type, Boolean local, String keyword, Pageable pageable);


    /**
     * 批量添加，修改，删除
     *
     * @param orgFlag
     * @param orgCdbVOS
     * @return
     */
    void saveOrgCdb(String orgFlag, List<OrgCdbVO> orgCdbVOS);
}
