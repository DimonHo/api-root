package com.wd.cloud.orgserver.service;

import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.orgserver.entity.IpRange;
import com.wd.cloud.orgserver.entity.Org;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
public interface OrgInfoService {

    Page<Org> getPageOrg(Pageable pageable);

    List<Org> getAll();

    OrgDTO get(Long id);

    OrgDTO findByIp(String ip);

    Org getOrgInfoByFlag(String flag);

    Map<IpRangeDTO, Set<IpRange>> cd();
}
