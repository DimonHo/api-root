package com.wd.cloud.orgserver.service.impl;

import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgRepository;
import com.wd.cloud.orgserver.service.OrgInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@Service("orgService")
@Transactional(rollbackFor = Exception.class)
public class OrgInfoServiceImpl implements OrgInfoService {

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    IpRangeRepository ipRangeRepository;

    @Override
    public Page<Org> getAllOrg(Pageable pageable) {
        return orgRepository.findAll(pageable);
    }

    @Override
    public Org get(Long id) {
        return orgRepository.getOne(id);
    }

    @Override
    public Org getOrgInfoByFlag(String flag) {
        return orgRepository.findByFlag(flag).orElse(null);
    }
}
