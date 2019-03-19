package com.wd.cloud.uoserver.service;

import com.wd.cloud.commons.dto.DepartmentDTO;
import com.wd.cloud.uoserver.entity.*;
import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
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

    List<DepartmentDTO> findByOrgId(Long orgId);

    Org getOrgId(Long orgId);

    Department insertDepartment(Long orgId, String name);

    Department updateDepartment(Long id,Long orgId, String name);

    void deleteDepartmentId(Long id);

    Page<OrgDTO> findByNameAndIp(Pageable pageable, String orgName, String ip);

    Page<OrgDTO> findByStatus(Integer status,Pageable pageable);

    Page<OrgDTO> notFindByStatus(Pageable pageable);

    OrgDTO findByOrgNameDetail(Long id);

    //修改学校基本信息接口
    void updateOrgAndLinkman(Long id,String orgName,String flag,String province,String city,String name,String email,String phone);

    //根据学校新增产品
    void insertOrgAndProduct(Long orgId, Long productId, Date beginDate, Date endDate, Integer status, Boolean single);

    //根据学校批量新增IP
    void insertOrgAndIpRangeS(Long orgId,String beginAndEnd);

    //新增学校基本信息
    Org insertOrgAndLinkman(String orgName,String flag,String province,String city,String name,String email,String phone);

    //根据学校删除IP
    void deleteIpRangeOrgId(Long orgId);
    //根据学校删除产品
    void deleteProductOrgId(Long orgId);


    //检测IP是否存在
    Boolean findIpRangesExist(String begin,String end);


    //查找机构标识是否已经存在
    Boolean findExistsFlag(String flag);

    //检测机构名是否存在
    Boolean findOrgNameExist(String name);

    //根据学校查询学校信息
    Org findByName(String name);


}
