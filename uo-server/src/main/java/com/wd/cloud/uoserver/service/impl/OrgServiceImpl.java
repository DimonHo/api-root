package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.dto.*;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.commons.util.NetUtil;
import com.wd.cloud.uoserver.exception.IPValidException;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.pojo.entity.*;
import com.wd.cloud.uoserver.pojo.vo.OrgIpVO;
import com.wd.cloud.uoserver.pojo.vo.OrgLinkmanVO;
import com.wd.cloud.uoserver.pojo.vo.OrgProductVO;
import com.wd.cloud.uoserver.pojo.vo.OrgVO;
import com.wd.cloud.uoserver.repository.*;
import com.wd.cloud.uoserver.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    IpRangeRepository ipRangeRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    OrgCdbRepository orgCdbRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrgProductRepository orgProductRepository;

    @Autowired
    LinkmanRepository linkmanRepository;

    /**
     * 校验IP是否是正确的格式，返回错误IP列表
     *
     * @return
     */
    @Override
    public List<IpRange> validatorIp() {
        List<IpRange> errorIps = new ArrayList<>();
        //查询所有数据
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        for (IpRange ipRange : ipRanges) {
            if (!Validator.isIpv4(ipRange.getBegin()) || !Validator.isIpv4(ipRange.getEnd())) {
                errorIps.add(ipRange);
            }
        }
        return errorIps;
    }


    /**
     * 翻转起始IP大于结束IP的记录
     */
    @Override
    public void reverse() {
        //查询所有数据
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        //根据Id查询开始IP跟结束IP
        for (IpRange ipRange : ipRanges) {
            String begin = ipRange.getBegin();
            String end = ipRange.getEnd();
            long beginNumber = NetUtil.ipv4ToLong(begin);
            long endNumber = NetUtil.ipv4ToLong(end);
            //如果开始IP比结束IP大则翻转他们的起始和结束
            if (beginNumber > endNumber) {
                ipRange.setBegin(end).setEnd(begin).setBeginNumber(endNumber).setEndNumber(beginNumber);
            } else {
                ipRange.setBegin(begin).setEnd(end).setBeginNumber(beginNumber).setEndNumber(endNumber);
            }
            ipRangeRepository.save(ipRange);
        }
    }

    /**
     * 查询重叠IP范围
     *
     * @return
     */
    @Override
    public Map<IpRangeDTO, Set<IpRange>> overlay() {
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        Map<IpRangeDTO, Set<IpRange>> orgIpMap = new HashMap<>();
        for (int i = 0; i < ipRanges.size(); i++) {
            IpRange ipRange1 = ipRanges.get(i);
            long beginIp1 = NetUtil.ipToLong(ipRange1.getBegin());
            long endIp1 = NetUtil.ipToLong(ipRange1.getEnd());
            for (int j = i + 1; j < ipRanges.size(); j++) {
                IpRange ipRange2 = ipRanges.get(j);
                long beginIp2 = NetUtil.ipToLong(ipRange2.getBegin());
                long endIp2 = NetUtil.ipToLong(ipRange2.getEnd());
                if (beginIp1 > beginIp2) {
                    beginIp2 = beginIp1;
                }
                if (endIp1 < endIp2) {
                    endIp2 = endIp1;
                }
                if (beginIp2 < endIp2) {
                    IpRangeDTO ipRangDTOKey = new IpRangeDTO();
                    ipRangDTOKey.setBegin(NetUtil.longToIp(beginIp2)).setEnd(NetUtil.longToIp(endIp2));
                    if (orgIpMap.get(ipRangDTOKey) != null) {
                        orgIpMap.get(ipRangDTOKey).add(ipRange1);
                        orgIpMap.get(ipRangDTOKey).add(ipRange2);
                    } else {
                        Set<IpRange> ips = new HashSet<>();
                        ips.add(ipRange1);
                        ips.add(ipRange2);
                        orgIpMap.put(ipRangDTOKey, ips);
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
    public void saveOrg(OrgVO orgVO) {
        Org org = orgRepository.findByFlag(orgVO.getFlag()).orElse(new Org());
        BeanUtil.copyProperties(orgVO, org);
        if (orgVO.getIp() != null) {
            saveOrgIp(orgVO.getFlag(),orgVO.getIp());
        }
        if (orgVO.getProduct() != null){
            saveOrgProduct(orgVO.getFlag(),orgVO.getProduct());
        }
        if (orgVO.getLinkman() != null){
            saveLinkman(orgVO.getFlag(),orgVO.getLinkman());
        }
        orgRepository.save(org);
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
    public OrgDTO findOrg(String orgName, String flag, String ip,List<String> includes) {
        Org org = orgRepository.findOne(OrgRepository.SpecificationBuilder.queryOrg(orgName, flag, ip, null, null, false))
                .orElseThrow(NotFoundOrgException::new);
        return convertOrgToDTO(org, null,null,false, includes);
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
     * @param orgProductVOS
     */
    @Override
    public List<OrgProduct> saveOrgProduct(String orgFlag, List<OrgProductVO> orgProductVOS) {
        List<OrgProduct> orgProducts = new ArrayList<>();
        for (OrgProductVO orgProductVO : orgProductVOS) {
            // 如果是删除
            if (orgProductVO.getDel()) {
                orgProductRepository.deleteByOrgFlagAndProductId(orgFlag, orgProductVO.getProductId());
                continue;
            }
            // 新增或修改
            OrgProduct orgProduct = orgProductRepository.findByOrgFlagAndProductId(orgFlag, orgProductVO.getProductId()).orElse(new OrgProduct());
            BeanUtil.copyProperties(orgProductVO, orgProduct);
            orgProduct.setOrgFlag(orgFlag);
            orgProducts.add(orgProduct);
        }
        return orgProductRepository.saveAll(orgProducts);
    }

    @Override
    public List<Linkman> saveLinkman(String orgFlag, List<OrgLinkmanVO> linkmanVOS) {
        List<Linkman> linkmanList = new ArrayList<>();
        for (OrgLinkmanVO linkmanVO : linkmanVOS) {
            // 如果是删除
            if (linkmanVO.getDel()) {
                linkmanRepository.deleteByOrgFlagAndId(orgFlag, linkmanVO.getId());
                continue;
            }
            // 新增或修改
            Linkman linkman = linkmanRepository.findByOrgFlagAndId(orgFlag, linkmanVO.getId()).orElse(new Linkman());
            BeanUtil.copyProperties(linkmanVO, linkman);
            linkman.setOrgFlag(orgFlag);
            linkmanList.add(linkman);
        }
        return linkmanRepository.saveAll(linkmanList);
    }

    /**
     * 取消某产品订购
     *
     * @param orgFlag
     * @param productIds
     */
    @Override
    public void cancelProduct(String orgFlag, List<Long> productIds) {
        //如果productIds为空，则取消订购所有产品
        if (productIds == null) {
            orgProductRepository.deleteByOrgFlag(orgFlag);
        }
        orgProductRepository.deleteByOrgFlagAndProductIdIn(orgFlag, productIds);
    }

    /**
     * 添加机构IP
     *
     * @param orgFlag
     * @param ipRange ex: “127.0.0.1---127.0.0.10;192.168.1.1---192.168.1.10”
     */
    @Override
    public List<IpRange> addOrgIp(String orgFlag, String ipRange) {
        List<IpRange> ipRangeList = new ArrayList<>();
        List<String> ipRanges = StrUtil.splitTrim(ipRange, ";");
        for (String ipRangeStr : ipRanges) {
            List<String> ips = StrUtil.splitTrim(ipRangeStr, "---");
            String beginIp = ips.get(0);
            String endIp = ips.get(1);
            // 检查IP格式是否合法
            if (!Validator.isIpv4(beginIp) || !Validator.isIpv4(endIp)) {
                throw IPValidException.validNotIp(ipRangeStr);
            }
            long beginNum = NetUtil.ipv4ToLong(beginIp);
            long endNum = NetUtil.ipv4ToLong(endIp);
            // 如果开始IP大于结束IP，将位置互换
            if (beginNum > endNum) {
                long temp = beginNum;
                beginNum = endNum;
                beginIp = ips.get(1);
                endNum = temp;
                endIp = ips.get(0);
            }
            // 查询IP是否已存在
            List<IpRange> overlayIpRanges = ipRangeRepository.findExists(beginNum, endNum);
            if (CollectionUtil.isNotEmpty(overlayIpRanges)) {
                throw IPValidException.existsIp(beginIp, endIp, overlayIpRanges);
            }
            // 通过检查
            IpRange ipRangeEntity = new IpRange();
            ipRangeEntity.setOrgFlag(orgFlag).setBegin(beginIp).setEnd(endIp).setBeginNumber(beginNum).setEndNumber(endNum);
            ipRangeList.add(ipRangeEntity);
        }
        return ipRangeRepository.saveAll(ipRangeList);
    }


    /**
     * 新增、修改或删除orgIpRange
     *
     * @param orgFlag
     * @param orgIpVOS
     * @return
     */
    @Override
    public List<IpRange> saveOrgIp(String orgFlag, List<OrgIpVO> orgIpVOS) {
        List<IpRange> ipRangeList = new ArrayList<>();
        for (OrgIpVO ipModel : orgIpVOS) {
            // 如果是删除
            if (ipModel.isDel() && ipModel.getId() != null) {
                ipRangeRepository.deleteByOrgFlagAndId(orgFlag, ipModel.getId());
                continue;
            }
            String beginIp = ipModel.getBegin();
            String endIp = ipModel.getEnd();
            //校验IP格式
            if (!Validator.isIpv4(beginIp) || !Validator.isIpv4(endIp)) {
                throw IPValidException.validNotIp(beginIp + "---" + endIp);
            }
            long beginNum = NetUtil.ipv4ToLong(beginIp);
            long endNum = NetUtil.ipv4ToLong(endIp);
            // 如果开始IP大于结束IP，将位置互换
            if (beginNum > endNum) {
                long temp = beginNum;
                beginNum = endNum;
                beginIp = ipModel.getEnd();
                endNum = temp;
                endIp = ipModel.getBegin();
            }
            // 查询IP是否已存在
            List<IpRange> overlayIpRanges = ipRangeRepository.findExists(beginNum, endNum);
            if (CollectionUtil.isNotEmpty(overlayIpRanges)) {
                throw IPValidException.existsIp(beginIp, endIp, overlayIpRanges);
            }
            // 通过检查，如果有id,则更新，否则就新增
            IpRange ipRangeEntity = ipRangeRepository.findByOrgFlagAndId(orgFlag, ipModel.getId()).orElse(new IpRange());
            ipRangeEntity.setOrgFlag(orgFlag).setBegin(beginIp).setEnd(endIp).setBeginNumber(beginNum).setEndNumber(endNum);
            ipRangeList.add(ipRangeEntity);
        }
        return ipRangeRepository.saveAll(ipRangeList);
    }


    @Override
    public List<DepartmentDTO> queryDepartments(String orgFlag) {
        List<DepartmentDTO> arrayDepartmentDTO = new ArrayList<>();
        List<Department> departments = departmentRepository.findByOrgFlag(orgFlag);
        Org byId = orgRepository.findByFlag(orgFlag).orElse(null);
        for (Department department : departments) {
            DepartmentDTO giveRecordDTO = new DepartmentDTO();
            giveRecordDTO.setOrgName(byId.getName());
            giveRecordDTO.setUserCount(0);
            BeanUtil.copyProperties(department, giveRecordDTO);
            arrayDepartmentDTO.add(giveRecordDTO);
        }
        return arrayDepartmentDTO;
    }


    @Override
    public void deleteDepartmentId(Long id) {
        departmentRepository.deleteById(id);
    }

    /**
     * org转换OrgDTO
     *
     * @param org
     * @param prodStatus 产品状态列表
     * @param includes   需要包含的关联对象 （"ip" or "product" or "linkman" or "department"）
     * @return
     */
    private OrgDTO convertOrgToDTO(Org org, List<Integer> prodStatus,Boolean isExp,boolean isFilter, List<String> includes) {
        OrgDTO orgDTO = BeanUtil.toBean(org, OrgDTO.class);
        if (CollectionUtil.isNotEmpty(includes)){
            for (String include : includes) {
                if ("ipRanges".equals(include)) {
                    includeIpRanges(org, orgDTO);
                }
                if ("products".equals(include)) {
                    // 如果isFilter为false,表示返回结果中不过滤产品状态和是否过期
                    if (!isFilter){
                        prodStatus = null;
                        isExp = null;
                    }
                    includeProducts(org, prodStatus, isExp, orgDTO);
                }
                if ("linkmans".equals(include)) {
                    includeLinkmans(org, orgDTO);
                }
                if ("departments".equals(include)) {
                    includeDepartments(org, orgDTO);
                }
            }
        }
        return orgDTO;
    }

    private void includeDepartments(Org org, OrgDTO orgDTO) {
        List<Department> departmentList = departmentRepository.findByOrgFlag(org.getFlag());
        List<DepartmentDTO> departmentDTOS = new ArrayList<>();
        departmentList.forEach(department -> {
            DepartmentDTO departmentDTO = BeanUtil.toBean(department, DepartmentDTO.class);
            departmentDTOS.add(departmentDTO);
        });
        orgDTO.setDepartments(departmentDTOS);
    }

    private void includeLinkmans(Org org, OrgDTO orgDTO) {
        List<Linkman> linkmanList = linkmanRepository.findByOrgFlag(org.getFlag());
        List<LinkmanDTO> linkmanDTOS = new ArrayList<>();
        linkmanList.forEach(linkman -> {
            LinkmanDTO linkmanDTO = BeanUtil.toBean(linkman, LinkmanDTO.class);
            linkmanDTOS.add(linkmanDTO);
        });
        orgDTO.setLinkmans(linkmanDTOS);
    }

    private void includeProducts(Org org, List<Integer> prodStatus, Boolean isExp, OrgDTO orgDTO) {
        // 如果isExp!=null且isExp==true?查询机构过期产品, isExp==false？查询机构未过期产品, isExp==null?查询机构所有产品
        List<OrgProduct> orgProducts = isExp!=null?
                isExp?orgProductRepository.findByOrgFlagAndExpDateBefore(org.getFlag(), DateUtil.date())
                        :orgProductRepository.findByOrgFlagAndEffDateBeforeAndExpDateAfter(org.getFlag(),DateUtil.date(),DateUtil.date())
                :orgProductRepository.findByOrgFlag(org.getFlag());
        List<ProductDTO> productDTOS = new ArrayList<>();
        List<Integer> finalProdStatus = prodStatus;
        orgProducts.stream()
                //prodStatus为空？返回所有结果，否则返回prodStatus状态列表中的结果
                .filter(orgProduct -> CollectionUtil.isEmpty(finalProdStatus) || finalProdStatus.contains(orgProduct.getStatus()))
                .forEach(orgProduct -> {
                    ProductDTO productDTO = BeanUtil.toBean(orgProduct, ProductDTO.class);
                    Optional<Product> optionalProduct = productRepository.findById(orgProduct.getProductId());
                    optionalProduct.ifPresent(product -> {
                        BeanUtil.copyProperties(product, productDTO);
                        productDTOS.add(productDTO);
                    });
                });
        orgDTO.setProducts(productDTOS);
    }

    private void includeIpRanges(Org org, OrgDTO orgDTO) {
        List<IpRange> ipRanges = ipRangeRepository.findByOrgFlag(org.getFlag());
        List<IpRangeDTO> ipRangeDTOS = new ArrayList<>();
        ipRanges.forEach(ipRange -> {
            IpRangeDTO ipRangeDTO = new IpRangeDTO();
            ipRangeDTO.setBegin(ipRange.getBegin()).setEnd(ipRange.getEnd());
            ipRangeDTOS.add(ipRangeDTO);
        });
        orgDTO.setIpRanges(ipRangeDTOS);
    }

}
