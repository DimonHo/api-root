package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.Org;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoService {

    List<Org> getAllOrg(String sortFiled);

    Org get(Long id);

    Org getOrgInfoByFlag(String flag);
}
