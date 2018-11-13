package com.wd.cloud.orgserver.service.impl;

import com.wd.cloud.orgserver.entity.OrgInfo;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgInfoRepository;
import com.wd.cloud.orgserver.service.OrgInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@Service("orgService")
public class OrgInfoServiceImpl implements OrgInfoService {

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    IpRangeRepository ipRangeRepository;

    @Override
    public List<OrgInfo> getAllOrg(String sort) {
        return orgInfoRepository.findAll(Sort.by(sort));
    }

    @Override
    public OrgInfo getOrgInfoByFlag(String flag) {
        return orgInfoRepository.findByDefaultFlag(flag).orElse(null);
    }
}
