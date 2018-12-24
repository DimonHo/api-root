package com.wd.cloud.orgserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.repository.IpRangeRepository;
import com.wd.cloud.orgserver.repository.OrgRepository;
import com.wd.cloud.orgserver.service.OrgInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Org getOrgInfoByFlag(String flag) {
        return orgRepository.findByFlag(flag).orElse(null);
    }
}
