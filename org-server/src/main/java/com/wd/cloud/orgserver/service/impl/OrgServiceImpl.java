package com.wd.cloud.orgserver.service.impl;

import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgRepository;
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
    OrgRepository orgRepository;

    @Autowired
    IpRangeRepository ipRangeRepository;

    @Override
    public Org getOrgInfoByFlag(String flag) {
        return orgRepository.findByOrgFlag(flag).orElse(null);
    }
}
