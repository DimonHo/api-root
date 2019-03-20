package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.DepartmentDTO;
import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.uoserver.pojo.entity.IpRange;
import com.wd.cloud.uoserver.pojo.entity.Linkman;
import com.wd.cloud.uoserver.pojo.entity.OrgProduct;
import com.wd.cloud.uoserver.pojo.vo.OrgIpVO;
import com.wd.cloud.uoserver.pojo.vo.OrgLinkmanVO;
import com.wd.cloud.uoserver.pojo.vo.OrgProductVO;
import com.wd.cloud.uoserver.pojo.vo.OrgVO;
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
     * 检查机构名称或标识是否已存在
     *
     * @param flag
     * @param name
     * @return
     */
    boolean orgExists(String flag, String name);

    /**
     * 保存机构信息
     * @param org
     */
    void saveOrg(OrgVO org);

    /**
     * 查询IP所属机构信息
     *
     * @param orgName
     * @param flag
     * @param ip
     * @return
     */
    OrgDTO findOrg(String orgName, String flag, String ip);

    /**
     * 查询机构列表
     *
     * @param orgName
     * @param flag
     * @param ip
     * @param prodStatus 产品状态
     * @param isExp 产品是否过期
     * @param pageable
     * @return
     */
    Page<OrgDTO> likeOrg(String orgName, String flag, String ip, List<Integer> prodStatus, Boolean isExp, boolean isFilter, Pageable pageable);

    /**
     * 新增，更新，删除订购产品的状态
     * @param orgFlag
     * @param productVOS
     * @return
     */
    List<OrgProduct> saveOrgProduct(String orgFlag, List<OrgProductVO> productVOS);

    /**
     * 新增,更新，刪除机构联系人
     * @param orgFlag
     * @param linkmanVOS
     * @return
     */
    List<Linkman> saveLinkman(String orgFlag, List<OrgLinkmanVO> linkmanVOS);

    /**
     * 取消订购产品
     *
     * @param orgFlag
     * @param productIds 取消的产品ID列表
     */
    void cancelProduct(String orgFlag, List<Long> productIds);

    /**
     * 添加机构IP
     *
     * @param orgFlag
     * @param ipRanges
     * @return
     */
    List<IpRange> addOrgIp(String orgFlag, String ipRanges);

    /**
     * 添加或修改机构IP
     *
     * @param orgFlag
     * @param ipModels
     * @return
     */
    List<IpRange> saveOrgIp(String orgFlag, List<OrgIpVO> ipModels);


    void deleteDepartmentId(Long id);

    /**
     * 查询部门
     * @param orgFlag
     */
    List<DepartmentDTO> queryDepartments(String orgFlag);





}
