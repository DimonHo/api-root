package com.wd.cloud.orgserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
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

import java.util.*;

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
        if (orgIpMap.size() > 1) {
            log.warn("查询到多个IP范围重叠：{}", JSONUtil.parseFromMap(orgIpMap).toStringPretty());
            //throw new NotOneOrgException(orgIpMap);
        }
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


    @Override
    public Map<IpRangDTO, Set<IpRange>> cd() {
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        Map<IpRangDTO, Set<IpRange>> orgIpMap = new HashMap<>();
        for (int i = 0; i < ipRanges.size(); i++) {
            IpRange ipRange1 = ipRanges.get(i);
            long beginIp = IpUtil.ipToLong(ipRange1.getBegin());
            long endIp = IpUtil.ipToLong(ipRange1.getEnd());
            if (beginIp > endIp) {
                long temp = beginIp;
                beginIp = endIp;
                endIp = temp;
            }
            for (int j = i + 1; j < ipRanges.size(); j++) {
                IpRange ipRange2 = ipRanges.get(j);
                long beginIp2 = IpUtil.ipToLong(ipRange2.getBegin());
                long endIp2 = IpUtil.ipToLong(ipRange2.getEnd());
                if (beginIp2 > endIp2) {
                    long temp2 = beginIp2;
                    beginIp2 = endIp2;
                    endIp2 = temp2;
                }

                if (beginIp > beginIp2) {
                    beginIp2 = beginIp;
                }
                if (endIp < endIp2) {
                    endIp2 = endIp;
                }
                if (beginIp2 < endIp2) {
                    IpRangDTO ipRangDTOKey = new IpRangDTO();
                    ipRangDTOKey.setBegin(IpUtil.longToIp(beginIp2)).setEnd(IpUtil.longToIp(endIp2));
                    if (orgIpMap.get(ipRangDTOKey) != null) {
                        orgIpMap.get(ipRangDTOKey).add(ipRange1);
                        orgIpMap.get(ipRangDTOKey).add(ipRange2);
                    } else {
                        Set<IpRange> ips = new HashSet<>();
                        ips.add(ipRange1);
                        ips.add(ipRange2);
                        orgIpMap.put(ipRangDTOKey, ips);
                    }
                }
            }

        }
        return orgIpMap;
    }
}
