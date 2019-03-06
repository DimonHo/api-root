package com.wd.cloud.uoserver.service;

import com.wd.cloud.uoserver.entity.IpRange;
import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgService {

    /**
     * 校验IP是否是正确的格式，返回错误IP列表
     *
     * @return
     */
    List<IpRange> validatorIp();

    /**
     * 翻转起始IP大于结束IP的记录
     */
    void reverse();

    /**
     * 查询重叠IP范围
     *
     * @return
     */
    Map<IpRangeDTO, Set<IpRange>> overlay();

    /**
     * 查询IP所属机构信息
     *
     * @param ip
     * @return
     */
    OrgDTO findOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip);


    Page<OrgDTO> likeOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip, Pageable pageable);

    OrgDTO addOrg();

    OrgDTO getOrg(Long orgId);
}
