package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.Org;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoService {

    Page<Org> getAllOrg(Pageable pageable);

    Org get(Long id);

    Org getOrgInfoByFlag(String flag);
}
