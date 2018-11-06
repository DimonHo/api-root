package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.entity.Org;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgService {

    Org getOrgInfoByFlag(String flag);
}
