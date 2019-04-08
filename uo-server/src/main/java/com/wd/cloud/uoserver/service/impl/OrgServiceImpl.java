package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.BooleanUtil;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.commons.util.NetUtil;
import com.wd.cloud.uoserver.exception.IPValidException;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.pojo.dto.*;
import com.wd.cloud.uoserver.pojo.entity.*;
import com.wd.cloud.uoserver.pojo.vo.*;
import com.wd.cloud.uoserver.repository.*;
import com.wd.cloud.uoserver.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.net.util.IPAddressUtil;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Slf4j
@Service("orgService")
@Transactional(rollbackFor = Exception.class)
public class OrgServiceImpl implements OrgService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrgIpRepository orgIpRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    OrgCdbRepository orgCdbRepository;

    @Autowired
    OrgDeptRepository orgDeptRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrgProdRepository orgProdRepository;

    @Autowired
    OrgLinkmanRepository orgLinkmanRepository;

    /**
     * 校验IP是否是正确的格式，返回错误IP列表
     *
     * @return
     */
    @Override
    public List<OrgIp> validatorIp() {
        List<OrgIp> errorIps = new ArrayList<>();
        //查询所有数据
        List<OrgIp> orgIps = orgIpRepository.findAll();
        for (OrgIp orgIp : orgIps) {
            if (!Validator.isIpv4(orgIp.getBegin()) || !Validator.isIpv4(orgIp.getEnd())) {
                errorIps.add(orgIp);
            }
        }
        return errorIps;
    }


    /**
     * 翻转起始IP大于结束IP的记录
     */
    @Override
    public void reverse() throws UnknownHostException {
        //查询所有数据
        List<OrgIp> orgIps = orgIpRepository.findAll();
        //根据Id查询开始IP跟结束IP
        for (OrgIp orgIp : orgIps) {
            String begin = orgIp.getBegin();
            String end = orgIp.getEnd();
            BigInteger beginNumber = NetUtil.ipToBigInteger(begin);
            BigInteger endNumber = NetUtil.ipToBigInteger(end);
            //如果开始IP比结束IP大则翻转他们的起始和结束
            if (beginNumber.compareTo(endNumber) > 0) {
                orgIp.setBegin(end).setEnd(begin).setBeginNumber(endNumber).setEndNumber(beginNumber);
            } else {
                orgIp.setBegin(begin).setEnd(end).setBeginNumber(beginNumber).setEndNumber(endNumber);
            }
            orgIpRepository.save(orgIp);
        }
    }

    /**
     * 查询重叠IP范围
     *
     * @return
     */
    @Override
    public Map<OrgIpDTO, Set<OrgIp>> overlay() throws UnknownHostException {
        List<OrgIp> orgIps = orgIpRepository.findAll();
        Map<OrgIpDTO, Set<OrgIp>> orgIpMap = new HashMap<>();
        for (int i = 0; i < orgIps.size(); i++) {
            OrgIp orgIp1 = orgIps.get(i);
            boolean isV6 = IPAddressUtil.isIPv6LiteralAddress(orgIp1.getBegin());
            BigInteger beginIp1 = NetUtil.ipToBigInteger(orgIp1.getBegin());
            BigInteger endIp1 = NetUtil.ipToBigInteger(orgIp1.getEnd());
            for (int j = i + 1; j < orgIps.size(); j++) {
                OrgIp orgIp2 = orgIps.get(j);
                BigInteger beginIp2 = NetUtil.ipToBigInteger(orgIp2.getBegin());
                BigInteger endIp2 = NetUtil.ipToBigInteger(orgIp2.getEnd());
                if (beginIp1.compareTo(beginIp2) > 0) {
                    beginIp2 = beginIp1;
                }
                if (endIp1.compareTo(endIp2) < 0) {
                    endIp2 = endIp1;
                }
                if (beginIp2.compareTo(endIp2) < 0) {
                    OrgIpDTO orgIpDTOKey = new OrgIpDTO();
                    orgIpDTOKey.setBegin(NetUtil.bigIntegerToIp(beginIp2, isV6)).setEnd(NetUtil.bigIntegerToIp(endIp2, isV6));
                    if (orgIpMap.get(orgIpDTOKey) != null) {
                        orgIpMap.get(orgIpDTOKey).add(orgIp1);
                        orgIpMap.get(orgIpDTOKey).add(orgIp2);
                    } else {
                        Set<OrgIp> ips = new HashSet<>();
                        ips.add(orgIp1);
                        ips.add(orgIp2);
                        orgIpMap.put(orgIpDTOKey, ips);
                    }
                }
            }
        }
        return orgIpMap;
    }


    /**
     * 机构名称或标识是否已存在
     *
     * @param orgFlag
     * @param name
     * @return
     */
    @Override
    public boolean orgExists(String orgFlag, String name) {
        return orgRepository.existsByFlagOrName(orgFlag, name);
    }


    /**
     * 新增、修改、删除机构信息
     * @param orgVO
     */
    @Override
    public void saveOrg(OrgVO orgVO) throws UnknownHostException {
        Org org = orgRepository.findByFlag(orgVO.getFlag()).orElse(new Org());
        BeanUtil.copyProperties(orgVO, org);
        orgRepository.save(org);
        if (CollectionUtil.isNotEmpty(orgVO.getIp())) {
            saveOrgIp(orgVO.getFlag(),orgVO.getIp());
        }
        if (CollectionUtil.isNotEmpty(orgVO.getProd())){
            saveOrgProd(orgVO.getFlag(),orgVO.getProd());
        }
        if (CollectionUtil.isNotEmpty(orgVO.getLinkman())){
            saveLinkman(orgVO.getFlag(),orgVO.getLinkman());
        }

    }

    /**
     * 精确查询机构信息
     *
     * @param orgName
     * @param flag
     * @param ip
     * @return
     */
    @Override
    public OrgDTO findOrg(String orgName, String flag, String ip, List<String> includes) {
        Org org = orgRepository.findOne(OrgRepository.SpecificationBuilder.queryOrg(orgName, flag, ip, null, null, false))
                .orElseThrow(NotFoundOrgException::new);
        return convertOrgToDTO(org, null,null,false, includes);
    }

    @Override
    public List<Org> getOrgList() {
        return orgRepository.findAll();
    }

    /**
     * 模糊查询机构列表
     *
     * @param orgName
     * @param flag
     * @param ip
     * @param pageable
     * @return
     */
    @Override
    public Page<OrgDTO> likeOrg(String orgName, String flag, String ip, List<Integer> prodStatus, Boolean isExp,boolean isFilter,List<String> includes, Pageable pageable) {
        Page<Org> orgPage = orgRepository.findAll(OrgRepository.SpecificationBuilder.queryOrg(orgName, flag, ip, prodStatus, isExp, true), pageable);
        return orgPage.map(org -> convertOrgToDTO(org, prodStatus,isExp, isFilter,includes));
    }

    /**
     * 新增、更新、删除产品
     *
     * @param orgFlag
     * @param orgProdVOS
     */
    @Override
    public void saveOrgProd(String orgFlag, List<OrgProdVO> orgProdVOS) {
        List<OrgProd> orgProds = new ArrayList<>();
        for (OrgProdVO orgProdVO : orgProdVOS) {
            // 如果是删除
            if (BooleanUtil.isTrue(orgProdVO.getDel()) && orgProdVO.getProdId() != null) {
                orgProdRepository.deleteByOrgFlagAndProdId(orgFlag, orgProdVO.getProdId());
            } else {
                // 新增或修改
                OrgProd orgProd = orgProdRepository.findByOrgFlagAndProdId(orgFlag, orgProdVO.getProdId()).orElse(new OrgProd());
                BeanUtil.copyProperties(orgProdVO, orgProd);
                orgProd.setOrgFlag(orgFlag);
                orgProds.add(orgProd);
            }
        }
        orgProdRepository.saveAll(orgProds);
    }

    @Override
    public void saveLinkman(String orgFlag, List<OrgLinkmanVO> linkmanVOS) {
        List<OrgLinkman> orgLinkmanList = new ArrayList<>();
        for (OrgLinkmanVO linkmanVO : linkmanVOS) {
            // 如果有id
            if (linkmanVO.getId() != null) {
                // 删除
                if (BooleanUtil.isTrue(linkmanVO.getDel())) {
                    orgLinkmanRepository.deleteByOrgFlagAndId(orgFlag, linkmanVO.getId());
                    // 更新
                } else {
                    OrgLinkman orgLinkman = orgLinkmanRepository.findByOrgFlagAndId(orgFlag, linkmanVO.getId()).orElseThrow(NotFoundException::new);
                    BeanUtil.copyProperties(linkmanVO, orgLinkman);
                    orgLinkman.setOrgFlag(orgFlag);
                    orgLinkmanList.add(orgLinkman);
                }
            } else {
                // 新增
                OrgLinkman orgLinkman = new OrgLinkman();
                BeanUtil.copyProperties(linkmanVO, orgLinkman);
                orgLinkman.setOrgFlag(orgFlag);
                orgLinkmanList.add(orgLinkman);
            }
        }
        orgLinkmanRepository.saveAll(orgLinkmanList);
    }

    /**
     * 取消某产品订购
     *
     * @param orgFlag
     * @param prodIds
     */
    @Override
    public void cancelProd(String orgFlag, List<Long> prodIds) {
        //如果prodIds为空，则取消订购所有产品
        if (CollectionUtil.isEmpty(prodIds)) {
            orgProdRepository.deleteByOrgFlag(orgFlag);
        }
        orgProdRepository.deleteByOrgFlagAndProdIdIn(orgFlag, prodIds);
    }

    /**
     * 新增、修改或删除orgIp
     *
     * @param orgFlag
     * @param orgIpVOS
     * @return
     */
    @Override
    public List<OrgIp> saveOrgIp(String orgFlag, List<OrgIpVO> orgIpVOS) throws UnknownHostException {
        // 检查orgFlag是否存在，不存在则抛出异常
        orgRepository.findByFlag(orgFlag).orElseThrow(NotFoundOrgException::new);
        List<OrgIp> orgIpList = new ArrayList<>();
        for (OrgIpVO orgIpVO : orgIpVOS) {
            if (orgIpVO.getId() != null && BooleanUtil.isTrue(orgIpVO.getDel())) {
                orgIpRepository.deleteByOrgFlagAndId(orgFlag, orgIpVO.getId());
                continue;
            }
            String beginIp = orgIpVO.getBegin();
            String endIp = orgIpVO.getEnd();

            //校验IP格式
            if (!NetUtil.isIp(beginIp) || !NetUtil.isIp(endIp)) {
                throw IPValidException.validNotIp(beginIp + "---" + endIp);
            }
            //是否是V6地址
            boolean isV6 = IPAddressUtil.isIPv6LiteralAddress(beginIp) || IPAddressUtil.isIPv6LiteralAddress(endIp);
            BigInteger beginNum = NetUtil.ipToBigInteger(beginIp);
            BigInteger endNum = NetUtil.ipToBigInteger(endIp);
            // 如果开始IP大于结束IP，将位置互换
            if (beginNum.compareTo(endNum) > 0) {
                BigInteger temp = beginNum;
                beginNum = endNum;
                beginIp = orgIpVO.getEnd();
                endNum = temp;
                endIp = orgIpVO.getBegin();
            }
            if (orgIpVO.getId() != null) {
                // 更新
                OrgIp orgIpEntity = orgIpRepository.findByOrgFlagAndId(orgFlag, orgIpVO.getId()).orElseThrow(NotFoundException::new);
                if (beginNum.compareTo(orgIpEntity.getBeginNumber()) < 0) {
                    Optional<OrgIp> optionOrgIp = orgIpRepository.findExists(beginNum);
                    if (optionOrgIp.isPresent()) {
                        throw IPValidException.existsIp(beginIp, endIp, optionOrgIp.get());
                    }
                }
                if (endNum.compareTo(orgIpEntity.getEndNumber()) > 0 && orgIpRepository.findExists(endNum).isPresent()) {
                    Optional<OrgIp> optionOrgIp = orgIpRepository.findExists(endNum);
                    if (optionOrgIp.isPresent()) {
                        throw IPValidException.existsIp(beginIp, endIp, optionOrgIp.get());
                    }
                }
                // 通过检查，更新
                orgIpEntity.setOrgFlag(orgFlag).setBegin(beginIp).setEnd(endIp).setBeginNumber(beginNum).setEndNumber(endNum).setV6(isV6);
                orgIpList.add(orgIpEntity);

            } else {
                // 新增
                List<OrgIp> overlayOrgIps = orgIpRepository.findExists(beginNum, endNum);
                if (CollectionUtil.isEmpty(overlayOrgIps)) {
                    // 通过检查，如果有id,则更新，否则就新增
                    OrgIp orgIpEntity = new OrgIp();
                    orgIpEntity.setOrgFlag(orgFlag).setBegin(beginIp).setEnd(endIp).setBeginNumber(beginNum).setEndNumber(endNum).setV6(isV6);
                    orgIpList.add(orgIpEntity);
                } else {
                    throw IPValidException.existsIp(beginIp, endIp, overlayOrgIps);
                }
            }
        }
        return orgIpRepository.saveAll(orgIpList);
    }

    /**
     * 查询机构院系列表
     * @param orgFlag
     * @return
     */
    @Override
    public List<OrgDeptDTO> findOrgDept(String orgFlag) {
        List<OrgDept> orgDeptList = orgDeptRepository.findByOrgFlag(orgFlag);
        return orgDeptList.stream().map(this::convertOrgDeptToOrgDeptDTO).collect(Collectors.toList());
    }

    /**
     * 新增、修改、删除院系
     * @param orgFlag
     * @param deptLit
     * @return
     */
    @Override
    public List<OrgDept> saveDept(String orgFlag,List<DeptVO> deptLit) {
        // 检查orgFlag是否存在，不存在则抛出异常
        orgRepository.findByFlag(orgFlag).orElseThrow(NotFoundOrgException::new);
        List<OrgDept> orgDeptList = new ArrayList<>();
        for (DeptVO deptVo : deptLit){
            if (deptVo.getId() != null) {
                //删除
                if (BooleanUtil.isTrue(deptVo.getDel())) {
                    orgDeptRepository.deleteByOrgFlagAndId(orgFlag, deptVo.getId());
                } else {
                    // 修改
                    OrgDept orgDept = orgDeptRepository.findByOrgFlagAndId(orgFlag, deptVo.getId()).orElseThrow(NotFoundException::new);
                    BeanUtil.copyProperties(deptVo, orgDept);
                    orgDept.setOrgFlag(orgFlag);
                    orgDeptList.add(orgDept);
                }
            } else {
                //新增
                OrgDept orgDept = new OrgDept();
                BeanUtil.copyProperties(deptVo, orgDept);
                orgDept.setOrgFlag(orgFlag);
                orgDeptList.add(orgDept);
            }
        }
        return orgDeptRepository.saveAll(orgDeptList);
    }


    @Override
    public void deleteOrgDeptId(Long id) {
        orgDeptRepository.deleteById(id);
    }

    /**
     * orgDept 转换 DTO
     * @param orgDept
     * @return
     */
    private OrgDeptDTO convertOrgDeptToOrgDeptDTO(OrgDept orgDept) {
        OrgDeptDTO orgDeptDTO = BeanUtil.toBean(orgDept,OrgDeptDTO.class);
        orgDeptDTO.setUserCount(userRepository.countByOrgDeptId(orgDept.getId()));
        return orgDeptDTO;
    }
    /**
     * org转换OrgDTO
     *
     * @param org
     * @param prodStatus 产品状态列表
     * @param includes   需要包含的关联对象 （"ip" or "linkman" or "dept" or "cdb"）
     * @return
     */
    private OrgDTO convertOrgToDTO(Org org, List<Integer> prodStatus,Boolean isExp,boolean isFilter, List<String> includes) {
        OrgDTO orgDTO = BeanUtil.toBean(org, OrgDTO.class);
        if (CollectionUtil.isNotEmpty(includes)){
            for (String include : includes) {
                if ("ip".equals(include)) {
                    includeIpList(org, orgDTO);
                }
                if ("prod".equals(include)) {
                    // 如果isFilter为false,表示返回结果中不过滤产品状态和是否过期
                    if (!isFilter){
                        prodStatus = null;
                        isExp = null;
                    }
                    includeProdList(org, prodStatus, isExp, orgDTO);
                }
                if ("linkman".equals(include)) {
                    includeLinkmanList(org, orgDTO);
                }
                if ("dept".equals(include)) {
                    includeOrgDeptList(org, orgDTO);
                }
                if ("cdb".equals(include)){
                    includeCdbList(org,orgDTO);
                }
            }
        }
        return orgDTO;
    }

    private void includeCdbList(Org org, OrgDTO orgDTO){
        List<OrgCdb> orgCdbList = orgCdbRepository.findByOrgFlag(org.getFlag());
        List<OrgCdbDTO> orgCdbDTOS = new ArrayList<>();
        orgCdbList.forEach(orgCdb -> {
            OrgCdbDTO orgCdbDTO = BeanUtil.toBean(orgCdb,OrgCdbDTO.class);
            orgCdbDTOS.add(orgCdbDTO);
        });
        orgDTO.setCdbList(orgCdbDTOS);
    }

    private void includeOrgDeptList(Org org, OrgDTO orgDTO) {
        List<OrgDept> orgDeptList = orgDeptRepository.findByOrgFlag(org.getFlag());
        List<OrgDeptDTO> orgDeptDTOS = new ArrayList<>();
        orgDeptList.forEach(orgDept -> {
            OrgDeptDTO orgDeptDTO = BeanUtil.toBean(orgDept, OrgDeptDTO.class);
            orgDeptDTOS.add(orgDeptDTO);
        });
        orgDTO.setDeptList(orgDeptDTOS);
    }

    private void includeLinkmanList(Org org, OrgDTO orgDTO) {
        List<OrgLinkman> orgLinkmanList = orgLinkmanRepository.findByOrgFlag(org.getFlag());
        List<OrgLinkmanDTO> orgLinkmanDTOS = new ArrayList<>();
        orgLinkmanList.forEach(orgLinkman -> {
            OrgLinkmanDTO orgLinkmanDTO = BeanUtil.toBean(orgLinkman, OrgLinkmanDTO.class);
            orgLinkmanDTOS.add(orgLinkmanDTO);
        });
        orgDTO.setLinkmanList(orgLinkmanDTOS);
    }

    private void includeProdList(Org org, List<Integer> prodStatus, Boolean isExp, OrgDTO orgDTO) {
        // 如果isExp!=null且isExp==true?查询机构过期产品, isExp==false？查询机构未过期产品, isExp==null?查询机构所有产品
        List<OrgProd> orgProds = isExp!=null?
                isExp? orgProdRepository.findByOrgFlagAndExpDateBefore(org.getFlag(), DateUtil.date())
                        : orgProdRepository.findByOrgFlagAndEffDateBeforeAndExpDateAfter(org.getFlag(),DateUtil.date(),DateUtil.date())
                : orgProdRepository.findByOrgFlag(org.getFlag());
        List<OrgProdDTO> orgProdDTOS = new ArrayList<>();
        List<Integer> finalProdStatus = prodStatus;
        orgProds.stream()
                //prodStatus为空？返回所有结果，否则返回prodStatus状态列表中的结果
                .filter(orgProd -> CollectionUtil.isEmpty(finalProdStatus) || finalProdStatus.contains(orgProd.getStatus()))
                .forEach(orgProd -> {
                    OrgProdDTO orgProdDTO = BeanUtil.toBean(orgProd, OrgProdDTO.class);
                    Optional<Product> optionalProduct = productRepository.findById(orgProd.getProdId());
                    optionalProduct.ifPresent(product -> {
                        BeanUtil.copyProperties(product, orgProdDTO);
                        orgProdDTOS.add(orgProdDTO);
                    });
                });
        orgDTO.setProdList(orgProdDTOS);
    }

    private void includeIpList(Org org, OrgDTO orgDTO) {
        List<OrgIp> orgIps = orgIpRepository.findByOrgFlag(org.getFlag());
        List<OrgIpDTO> orgIpDTOS = new ArrayList<>();
        orgIps.forEach(orgIp -> {
            OrgIpDTO orgIpDTO = BeanUtil.toBean(orgIp,OrgIpDTO.class);
            orgIpDTOS.add(orgIpDTO);
        });
        orgDTO.setIpList(orgIpDTOS);
    }

}
