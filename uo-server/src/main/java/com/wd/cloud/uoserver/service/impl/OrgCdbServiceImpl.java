package com.wd.cloud.uoserver.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.wd.cloud.commons.dto.CdbDTO;
import com.wd.cloud.uoserver.entity.*;
import com.wd.cloud.uoserver.repository.CdbRepository;
import com.wd.cloud.uoserver.repository.OrgCdbRepository;
import com.wd.cloud.uoserver.repository.OrgRepository;
import com.wd.cloud.uoserver.service.OrgCdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("orgCdbService")
@Transactional(rollbackFor = Exception.class)
public class OrgCdbServiceImpl implements OrgCdbService {
    
    @Autowired
    OrgCdbRepository orgCdbRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    CdbRepository cdbRepository;


    @Override
    public Page<CdbDTO> findByOrgIdAndCollection(Pageable pageable, Long orgId, Boolean collection) {

        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecificationBuilder.findByOrgIdAndCollection(orgId, collection), pageable);
        Org org = orgRepository.findById(orgId).orElse(null);
        Page<CdbDTO> cdbDTOS = orgCdbs.map(orgCdb -> {
            Cdb cdb = cdbRepository.findById(orgCdb.getCdbId()).orElse(null);
            CdbDTO cdbDTO = new CdbDTO();
            cdbDTO.setFlag(org.getFlag());
            cdbDTO.setName(cdb.getName());
            cdbDTO.setUrl(cdb.getUrl());
            BeanUtil.copyProperties(orgCdb, cdbDTO);
            return cdbDTO;
        });
        return cdbDTOS;
    }

    @Override
    public Page<CdbDTO> findByOrgIdAndLocalUrlIsNotNull(Pageable pageable,Long orgId) {
        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecificationBuilder.findByOrgIdAndLocalUrlIsNotNull(orgId), pageable);
        Page<CdbDTO> cdbDTOS = orgCdbs.map(orgCdb -> {
            Cdb byId = cdbRepository.findById(orgCdb.getCdbId()).orElse(null);
            CdbDTO cdbDTO = new CdbDTO();
            cdbDTO.setName(byId.getName());
            cdbDTO.setUrl(byId.getUrl());
            BeanUtil.copyProperties(orgCdb, cdbDTO);
            return cdbDTO;
        });
        return cdbDTOS;
    }

    @Override
    public Page<CdbDTO> findByNameAndUrl(Pageable pageable, String keyword) {
        Page<Cdb> cdbs = cdbRepository.findAll(CdbRepository.SpecificationBuilder.findByNameAndUrl(keyword), pageable);
        Page<CdbDTO> cdbDTOS = cdbs.map(cdb -> {
            List<OrgCdb> byCdbId = orgCdbRepository.findByCdbId(cdb.getId());
            for (OrgCdb orgCdb : byCdbId){
                 CdbDTO cdbDTO = new CdbDTO();
                 Org org = orgRepository.findById(orgCdb.getOrgId()).orElse(null);
                 cdbDTO.setOrgName(org.getName());
                 cdbDTO.setName(cdb.getName());
                 cdbDTO.setUrl(cdb.getUrl());
                 return cdbDTO;
            }
            return null;
        });
        return cdbDTOS;
    }

    @Override
    public void updateOrgCdb(Long id, String name, String url, Boolean display) {
        OrgCdb orgCdb = new OrgCdb();
        Cdb cdb = new Cdb();
        OrgCdb byId = orgCdbRepository.findById(id).orElse(null);
        orgCdb.setId(id);
        orgCdb.setLocalUrl(byId.getLocalUrl());
        orgCdb.setCdbId(byId.getCdbId());
        orgCdb.setOrgId(byId.getOrgId());
        orgCdb.setGmtCreate(byId.getGmtCreate());
        orgCdb.setDisplay(display);

        cdb.setId(byId.getCdbId());
        Cdb cdbId = cdbRepository.findById(cdb.getId()).orElse(null);
        cdb.setName(name);
        cdb.setUrl(url);
        cdb.setGmtCreate(cdbId.getGmtCreate());
        orgCdbRepository.save(orgCdb);
        cdbRepository.save(cdb);
    }

    @Override
    public void insertOrgCdb(String name, String url, Long orgId, Boolean display) {
        OrgCdb orgCdb = new OrgCdb();
        Cdb cdb = new Cdb();
        cdb.setName(name);
        cdb.setUrl(url);
        cdbRepository.save(cdb);

        Cdb cdbNameUrl = cdbRepository.findByNameAndUrl(name, url);
        orgCdb.setDisplay(display);
        orgCdb.setOrgId(orgId);
        orgCdb.setCdbId(cdbNameUrl.getId());
        orgCdbRepository.save(orgCdb);
    }

    @Override
    public void deleteOrgCdb(Long id) {
        OrgCdb byId = orgCdbRepository.findById(id).orElse(null);
        Long cdbId = byId.getCdbId();
        cdbRepository.deleteById(cdbId);
        orgCdbRepository.deleteById(id);
    }

    @Override
    public void insertCdbUrl(String name, String url, Long orgId, String localUrl) {
        OrgCdb orgCdb = new OrgCdb();
        Cdb cdb = new Cdb();
        cdb.setName(name);
        cdb.setUrl(url);
        cdbRepository.save(cdb);

        Cdb cdbNameUrl = cdbRepository.findByNameAndUrl(name, url);
        orgCdb.setOrgId(orgId);
        orgCdb.setCdbId(cdbNameUrl.getId());
        orgCdb.setLocalUrl(localUrl);
        orgCdbRepository.save(orgCdb);
    }


}
