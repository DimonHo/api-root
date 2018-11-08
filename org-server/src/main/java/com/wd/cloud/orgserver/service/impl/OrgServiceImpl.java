package com.wd.cloud.orgserver.service.impl;

import com.wd.cloud.orgserver.entity.OrgInfo;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgInfoRepository;
import com.wd.cloud.orgserver.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@Service("orgService")
public class OrgServiceImpl implements OrgService {

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    IpRangeRepository ipRangeRepository;

    @Override
    public OrgInfo getOrgInfoByFlag(String flag) {
        return orgInfoRepository.findByDefaultFlag(flag).orElse(null);
    }
}
