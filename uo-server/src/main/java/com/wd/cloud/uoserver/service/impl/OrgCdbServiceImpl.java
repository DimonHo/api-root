package com.wd.cloud.uoserver.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.pojo.dto.OrgCdbDTO;
import com.wd.cloud.uoserver.pojo.entity.Org;
import com.wd.cloud.uoserver.pojo.entity.OrgCdb;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import com.wd.cloud.uoserver.pojo.entity.OrgProd;
import com.wd.cloud.uoserver.pojo.vo.OrgCdbVO;
import com.wd.cloud.uoserver.repository.OrgCdbRepository;
import com.wd.cloud.uoserver.repository.OrgIpRepository;
import com.wd.cloud.uoserver.repository.OrgProdRepository;
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
    
    @Autowired
    OrgIpRepository orgIpRepository;
    
    @Autowired
    OrgProdRepository orgProdRepository;

    /**
     * 查询机构馆藏数据库
     *
     * @param pageable
     * @param orgFlag
     * @param type 资源类型 1馆藏，2筛选
     * @param keyword 模糊查询关键字
     * @return
     * @Param local 是否有localUrl
     */
    @Override
    public Page<OrgCdbDTO> findOrgCdbs(String orgFlag, Integer type, Boolean local, String keyword, Pageable pageable) {
        Org org = orgRepository.findByFlag(orgFlag).orElseThrow(NotFoundOrgException::new);
        Page<OrgCdb> orgCdbs = orgCdbRepository.findAll(OrgCdbRepository.SpecBuilder.query(orgFlag, type, local, keyword), pageable);
        return orgCdbs.map(orgCdb -> convertOrgCdbToOrgCdbDTO(org, orgCdb));
    }


    /**
     * 批量添加，修改，删除
     *
     * @param orgFlag
     * @param orgCdbVOS
     * @return
     */
    @Override
    public void saveOrgCdb(String orgFlag, List<OrgCdbVO> orgCdbVOS) {
        List<OrgCdb> orgCdbList = new ArrayList<>();
        for (OrgCdbVO orgCdbVO : orgCdbVOS) {
            if (orgCdbVO.getId() != null){
                // 删除
                if (BooleanUtil.isTrue(orgCdbVO.getDel())){
                    orgCdbRepository.deleteByOrgFlagAndId(orgFlag, orgCdbVO.getId());
                }else{
                    // 更新
                    OrgCdb orgCdb = orgCdbRepository.findByOrgFlagAndId(orgFlag, orgCdbVO.getId()).orElse(new OrgCdb());
                    BeanUtil.copyProperties(orgCdbVO, orgCdb);
                    orgCdb.setOrgFlag(orgFlag);
                    orgCdbList.add(orgCdb);
                }
            }else{
                // 新增
                OrgCdb orgCdb = new OrgCdb();
                BeanUtil.copyProperties(orgCdbVO, orgCdb);
                orgCdb.setOrgFlag(orgFlag);
                orgCdbList.add(orgCdb);
            }
        }
        orgCdbRepository.saveAll(orgCdbList);
    }

    @Override
    public void deleteIpAndProd(String orgFlag) {
        List<OrgIp> orgIpList = orgIpRepository.findByOrgFlag(orgFlag);
        for (OrgIp orgIp : orgIpList){
            Long id = orgIp.getId();
            orgIpRepository.deleteById(id);
        }
        List<OrgProd> orgProdList = orgProdRepository.findByOrgFlag(orgFlag);
        for (OrgProd orgProd :orgProdList){
            Long id = orgProd.getId();
            orgProdRepository.deleteById(id);
        }

    }

    private OrgCdbDTO convertOrgCdbToOrgCdbDTO(Org org, OrgCdb orgCdb) {
        OrgCdbDTO orgCdbDTO = BeanUtil.toBean(orgCdb, OrgCdbDTO.class);
        orgCdbDTO.setOrgName(org.getName());
        return orgCdbDTO;
    }

}
