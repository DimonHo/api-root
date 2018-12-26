package com.wd.cloud.orgserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.common.collect.Lists;
import com.wd.cloud.commons.util.IpUtil;
import com.wd.cloud.orgserver.dto.IpRangDTO;
import com.wd.cloud.orgserver.dto.OrgBasicDTO;
import com.wd.cloud.orgserver.entity.IpRange;
import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.exception.NotFoundOrgException;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgRepository;
import com.wd.cloud.orgserver.service.OrgInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@Service("orgService")
@Transactional(rollbackFor = Exception.class)
public class OrgInfoServiceImpl implements OrgInfoService {

    private static final Log log = LogFactory.get();

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    IpRangeRepository ipRangeRepository;

    @Override
    public Page<Org> getPageOrg(Pageable pageable) {
        return orgRepository.findAll(pageable);
    }

    @Override
    public List<Org> getAll() {
        return orgRepository.findAll();
    }

    @Override
    public Org get(Long id) {
        return orgRepository.getOne(id);
    }

    @Override
    public OrgBasicDTO findByIp(String ip) {
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        Map<Long, List<IpRange>> orgIpMap = new HashMap<>();
        for (IpRange ipRange : ipRanges) {
            String beginIp = ipRange.getBegin();
            String endIp = ipRange.getEnd();
            if (IpUtil.isInner(ip, beginIp, endIp)) {
                log.info("begin={},end={}", beginIp, endIp);
                Long orgId = ipRange.getOrgId();
                if (orgIpMap.get(orgId) == null) {
                    orgIpMap.put(orgId, Lists.newArrayList(ipRange));
                } else {
                    orgIpMap.get(orgId).add(ipRange);
                }
            }
        }
//        if (orgIpMap.size() > 1) {
//            throw new NotOneOrgException(orgIpMap);
//        }
        OrgBasicDTO orgBasicDTO = null;
        for (Map.Entry<Long, List<IpRange>> entry : orgIpMap.entrySet()) {
            Long orgId = entry.getKey();
            Org org = orgRepository.findById(orgId).orElse(null);
            orgBasicDTO = new OrgBasicDTO();
            BeanUtil.copyProperties(org, orgBasicDTO);
            for (IpRange ipRange : ipRangeRepository.findByOrgId(orgId)) {
                IpRangDTO ipRangDTO = new IpRangDTO();
                BeanUtil.copyProperties(ipRange, ipRangDTO);
                orgBasicDTO.getIpRang().add(ipRangDTO);
            }
        }
        if (orgBasicDTO == null) {
            throw new NotFoundOrgException();
        }
        return orgBasicDTO;
    }

    @Override
    public Org getOrgInfoByFlag(String flag) {
        return orgRepository.findByFlag(flag).orElse(null);
    }
}
