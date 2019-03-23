package com.wd.cloud.uoserver.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.pojo.dto.OrgCdbDTO;
import com.wd.cloud.uoserver.pojo.entity.Org;
import com.wd.cloud.uoserver.pojo.entity.OrgCdb;
import com.wd.cloud.uoserver.pojo.vo.OrgCdbVO;
import com.wd.cloud.uoserver.repository.OrgCdbRepository;
import com.wd.cloud.uoserver.repository.OrgRepository;
import com.wd.cloud.uoserver.service.OrgCdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("orgCdbService")
@Transactional(rollbackFor = Exception.class)
public class OrgCdbServiceImpl implements OrgCdbService {

    @Autowired
    OrgCdbRepository orgCdbRepository;

    @Autowired
    OrgRepository orgRepository;


    @Override
    public Page<OrgCdbDTO> findOrgCdbs(String orgFlag, Boolean collection, Boolean local, String keyword, Pageable pageable) {
        Org org = orgRepository.findByFlag(orgFlag).orElseThrow(NotFoundOrgException::new);
        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecBuilder.query(orgFlag, collection, local, keyword), pageable);
        return orgCdbs.map(orgCdb -> convertOrgCdbToOrgCdbDTO(org, orgCdb));
    }

    private OrgCdbDTO convertOrgCdbToOrgCdbDTO(Org org, OrgCdb orgCdb) {
        OrgCdbDTO orgCdbDTO = BeanUtil.toBean(orgCdb, OrgCdbDTO.class);
        orgCdbDTO.setOrgName(org.getName());
        return orgCdbDTO;
    }

    @Override
    public void saveOrgCdb(String orgFlag, OrgCdbVO orgCdbVO) {
        if (orgCdbVO.isDel() && orgCdbVO.getId() != null) {
            orgCdbRepository.deleteByOrgFlagAndId(orgFlag, orgCdbVO.getId());
        } else {
            OrgCdb orgCdb = orgCdbRepository.findByOrgFlagAndId(orgFlag, orgCdbVO.getId()).orElse(new OrgCdb());
            BeanUtil.copyProperties(orgCdbVO, orgCdb);
            orgCdbRepository.save(orgCdb);
        }

    }

    @Override
    public void saveOrgCdb(String orgFlag, List<OrgCdbVO> orgCdbVOS) {
        List<OrgCdb> orgCdbList = new ArrayList<>();
        for (OrgCdbVO orgCdbVO : orgCdbVOS) {
            if (orgCdbVO.isDel() && orgCdbVO.getId() != null) {
                orgCdbRepository.deleteByOrgFlagAndId(orgFlag, orgCdbVO.getId());
                continue;
            }
            OrgCdb orgCdb = orgCdbRepository.findByOrgFlagAndId(orgFlag, orgCdbVO.getId()).orElse(new OrgCdb());
            BeanUtil.copyProperties(orgCdbVO, orgCdb);
            orgCdbList.add(orgCdb);
        }
        orgCdbRepository.saveAll(orgCdbList);
    }

}
