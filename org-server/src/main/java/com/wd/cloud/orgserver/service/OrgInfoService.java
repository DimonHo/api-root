package com.wd.cloud.orgserver.service;

import com.wd.cloud.orgserver.dto.IpRangDTO;
import com.wd.cloud.orgserver.dto.OrgBasicDTO;
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

    Org get(Long id);

    OrgBasicDTO findByIp(String ip);

    Org getOrgInfoByFlag(String flag);

    Map<IpRangDTO, Set<IpRange>> cd();
}
