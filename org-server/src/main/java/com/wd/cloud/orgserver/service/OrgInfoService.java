package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.OrgInfo;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoService {

    List<OrgInfo> getAllOrg(String sort);

    OrgInfo getOrgInfoByFlag(String flag);
}
