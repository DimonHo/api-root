package com.wd.cloud.uoserver.service;


import com.wd.cloud.uoserver.pojo.dto.OrgDTO;
import com.wd.cloud.uoserver.pojo.dto.OrgDeptDTO;
import com.wd.cloud.uoserver.pojo.dto.OrgIpDTO;
import com.wd.cloud.uoserver.pojo.entity.Org;
import com.wd.cloud.uoserver.pojo.entity.OrgDept;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import com.wd.cloud.uoserver.pojo.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface OrgService {

    /**
     * 查询所有IP列表
     *
     * @return
     */
    List<OrgIp> findAllOrgIp();

    /**
     * 查找IP所在范围
     *
     * @param ip
     * @return
     */
    Optional<OrgIp> findIp(String ip);

    /**
     * 校验IP是否是正确的格式，返回错误IP列表
     *
     * @return
     */
    List<OrgIp> validatorIp();

    /**
     * 翻转起始IP大于结束IP的记录
     */
    void reverse();

    /**
     * 查询重叠IP范围
     *
     * @return
     */
    Map<OrgIpDTO, Set<OrgIp>> overlay();

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
     *
     * @param org
     */
    void saveOrg(OrgVO org);

    /**
     * 查询IP所属机构信息
     *
     * @param orgName
     * @param flag
     * @return
     */
    OrgDTO findOrg(String orgName, String flag);


    /**
     * 获取机构列表
     *
     * @param sort
     * @return
     */
    List<Org> getOrgList(Sort sort);

    /**
     * 查询机构列表
     *
     * @param orgName
     * @param flag
     * @param ip
     * @param prodStatus
     * @param isExp
     * @param isFilter
     * @param result
     * @param pageable
     * @return
     */
    Page<OrgDTO> likeOrg(String orgName, String flag, String ip, List<Integer> prodStatus, Boolean isExp, boolean isFilter, List<String> result, Pageable pageable);

    /**
     * 新增，更新，删除订购产品的状态
     *
     * @param orgFlag
     * @param orgProdVOS
     * @return
     */
    void saveOrgProd(String orgFlag, List<OrgProdVO> orgProdVOS);

    /**
     * 新增,更新，刪除机构联系人
     *
     * @param orgFlag
     * @param linkmanVOS
     * @return
     */
    void saveLinkman(String orgFlag, List<OrgLinkmanVO> linkmanVOS);

    /**
     * 取消订购产品
     *
     * @param orgFlag
     * @param prodIds 取消的产品ID列表
     */
    void cancelProd(String orgFlag, List<Long> prodIds);

    /**
     * 添加或修改机构IP
     *
     * @param orgFlag
     * @param ipModels
     * @return
     */
    List<OrgIp> saveOrgIp(String orgFlag, List<OrgIpVO> ipModels);

    /**
     * 查询机构院系列表
     *
     * @param orgFlag
     * @return
     */
    List<OrgDeptDTO> findOrgDept(String orgFlag);

    /**
     * 新增、修改、删除院系
     *
     * @param orgFlag
     * @param deptLit
     * @return
     */
    List<OrgDept> saveDept(String orgFlag, List<DeptVO> deptLit);

    /**
     * 删除院系
     *
     * @param id
     */
    void deleteOrgDeptId(Long id);


}
