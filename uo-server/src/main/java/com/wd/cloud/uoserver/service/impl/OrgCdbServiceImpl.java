package com.wd.cloud.uoserver.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.wd.cloud.uoserver.pojo.dto.OrgCdbDTO;
import com.wd.cloud.uoserver.pojo.entity.Cdb;
import com.wd.cloud.uoserver.pojo.entity.Org;
import com.wd.cloud.uoserver.pojo.entity.OrgCdb;
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
    public Page<OrgCdbDTO> findByOrgFlagAndCollection(Pageable pageable, String orgFlag, Boolean collection) {

        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecificationBuilder.findByOrgFlagAndCollection(orgFlag, collection), pageable);
        Org org = orgRepository.findByFlag(orgFlag).orElse(null);
        Page<OrgCdbDTO> cdbDTOS = orgCdbs.map(orgCdb -> {
            Cdb cdb = cdbRepository.findById(orgCdb.getCdbId()).orElse(null);
            OrgCdbDTO orgCdbDTO = new OrgCdbDTO();
            orgCdbDTO.setOrgFlag(org.getFlag());
            orgCdbDTO.setName(cdb.getName());
            orgCdbDTO.setUrl(cdb.getUrl());
            BeanUtil.copyProperties(orgCdb, orgCdbDTO);
            return orgCdbDTO;
        });
        return cdbDTOS;
    }

    @Override
    public Page<OrgCdbDTO> findByOrgFlagAndLocalUrlIsNotNull(Pageable pageable, String orgFlag) {
        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecificationBuilder.findByOrgFlagAndLocalUrlIsNotNull(orgFlag), pageable);
        Page<OrgCdbDTO> cdbDTOS = orgCdbs.map(orgCdb -> {
            Cdb byId = cdbRepository.findById(orgCdb.getCdbId()).orElse(null);
            OrgCdbDTO orgCdbDTO = new OrgCdbDTO();
            orgCdbDTO.setName(byId.getName());
            orgCdbDTO.setUrl(byId.getUrl());
            BeanUtil.copyProperties(orgCdb, orgCdbDTO);
            return orgCdbDTO;
        });
        return cdbDTOS;
    }

    @Override
    public Page<OrgCdbDTO> findByNameAndUrl(Pageable pageable, String keyword) {
        Page<Cdb> cdbs = cdbRepository.findAll(CdbRepository.SpecificationBuilder.findByNameAndUrl(keyword), pageable);
        Page<OrgCdbDTO> cdbDTOS = cdbs.map(cdb -> {
            List<OrgCdb> byCdbId = orgCdbRepository.findByCdbId(cdb.getId());
            for (OrgCdb orgCdb : byCdbId){
                 OrgCdbDTO orgCdbDTO = new OrgCdbDTO();
                 Org org = orgRepository.findByFlag(orgCdb.getOrgFlag()).orElse(null);
                 orgCdbDTO.setOrgName(org.getName());
                 orgCdbDTO.setName(cdb.getName());
                 orgCdbDTO.setUrl(cdb.getUrl());
                 return orgCdbDTO;
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
        orgCdb.setOrgFlag(byId.getOrgFlag());
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
    public void insertOrgCdb(String name, String url, String orgFlag, Boolean display) {
        OrgCdb orgCdb = new OrgCdb();
        Cdb cdb = new Cdb();
        cdb.setName(name);
        cdb.setUrl(url);
        cdbRepository.save(cdb);

        Cdb cdbNameUrl = cdbRepository.findByNameAndUrl(name, url);
        orgCdb.setDisplay(display);
        orgCdb.setOrgFlag(orgFlag);
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
    public void insertCdbUrl(String name, String url, String orgFlag, String localUrl) {
        OrgCdb orgCdb = new OrgCdb();
        Cdb cdb = new Cdb();
        cdb.setName(name);
        cdb.setUrl(url);
        cdbRepository.save(cdb);

        Cdb cdbNameUrl = cdbRepository.findByNameAndUrl(name, url);
        orgCdb.setOrgFlag(orgFlag);
        orgCdb.setCdbId(cdbNameUrl.getId());
        orgCdb.setLocalUrl(localUrl);
        orgCdbRepository.save(orgCdb);
    }


}
