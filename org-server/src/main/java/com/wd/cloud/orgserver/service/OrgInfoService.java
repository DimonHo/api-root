package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.Org;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoService {

    Page<Org> getPageOrg(Pageable pageable);

    List<Org> getAll();

    Org get(Long id);

    Org getOrgInfoByFlag(String flag);
}
