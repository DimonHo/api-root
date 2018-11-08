package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.OrgInfo;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgService {

    OrgInfo getOrgInfoByFlag(String flag);
}
