package com.wd.cloud.orgserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.common.collect.Lists;
import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.util.IpUtil;
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
import java.util.stream.Collectors;

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
    public OrgDTO get(Long id) {
        Optional<Org> optionalOrg = orgRepository.findById(id);
        OrgDTO orgDTO = null;
        if (optionalOrg.isPresent()) {
            Org org = optionalOrg.get();
            List<IpRange> ipRanges = ipRangeRepository.findByOrgId(org.getId());
            List<IpRangeDTO> ipRangeDTOS = ipRanges.stream().map(ipRange -> {
                IpRangeDTO ipRangeDTO = new IpRangeDTO();
                BeanUtil.copyProperties(ipRange, ipRangeDTO);
                return ipRangeDTO;
            }).collect(Collectors.toList());
            orgDTO = new OrgDTO();
            BeanUtil.copyProperties(optionalOrg.get(), orgDTO);
            orgDTO.setIpRanges(ipRangeDTOS);
        }
        return orgDTO;
    }

    @Override
    public OrgDTO findByIp(String ip) {
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
        OrgDTO orgDTO = null;
        for (Map.Entry<Long, List<IpRange>> entry : orgIpMap.entrySet()) {
            Long orgId = entry.getKey();
            Org org = orgRepository.findById(orgId).orElse(null);
            orgDTO = new OrgDTO();
            BeanUtil.copyProperties(org, orgDTO);
            for (IpRange ipRange : ipRangeRepository.findByOrgId(orgId)) {
                IpRangeDTO ipRangeDTO = new IpRangeDTO();
                BeanUtil.copyProperties(ipRange, ipRangeDTO);
                if (orgDTO.getIpRanges() == null) {
                    orgDTO.setIpRanges(CollectionUtil.newArrayList(ipRangeDTO));
                } else {
                    orgDTO.getIpRanges().add(ipRangeDTO);
                }
            }
        }
        if (orgDTO == null) {
            throw new NotFoundOrgException();
        }
        return orgDTO;
    }

    @Override
    public Org getOrgInfoByFlag(String flag) {
        return orgRepository.findByFlag(flag).orElse(null);
    }


    @Override
    public Map<IpRangeDTO, Set<IpRange>> cd() {
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        Map<IpRangeDTO, Set<IpRange>> orgIpMap = new HashMap<>();
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
                    IpRangeDTO ipRangDTOKey = new IpRangeDTO();
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

    @Override
    public void findByBeginAndEnd() {
        //查询所有数据
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        //根据Id查询开始IP跟结束IP
        for (IpRange ipRange:ipRanges){
            String begin = ipRange.getBegin();
            String end = ipRange.getEnd();
            long beginIp = IpUtil.ipToLong(begin);
            long endIp = IpUtil.ipToLong(end);
            if (beginIp>endIp){//如果开始IP比结束IP大则替换他们的IP
                ipRange.setBegin(end);
                ipRange.setEnd(begin);
                ipRangeRepository.save(ipRange);
            }
        }
    }


}
