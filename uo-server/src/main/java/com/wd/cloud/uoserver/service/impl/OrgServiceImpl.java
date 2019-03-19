package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import com.wd.cloud.commons.dto.DepartmentDTO;
import com.wd.cloud.commons.dto.IpRangeDTO;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.ProductDTO;
import com.wd.cloud.commons.util.NetUtil;
import com.wd.cloud.uoserver.entity.*;
import com.wd.cloud.uoserver.repository.*;
import com.wd.cloud.uoserver.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
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
    DepartmentRepository departmentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrgProductRepository orgProductRepository;

    @Autowired
    LinkmanRepository linkmanRepository;

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

    @Override
    public void reverse() {
        //查询所有数据
        List<IpRange> ipRanges = ipRangeRepository.findAll();
        //根据Id查询开始IP跟结束IP
        for (IpRange ipRange : ipRanges) {
            String begin = ipRange.getBegin();
            String end = ipRange.getEnd();
            long beginNumber = cn.hutool.core.util.NetUtil.ipv4ToLong(begin);
            long endNumber = cn.hutool.core.util.NetUtil.ipv4ToLong(end);
            //如果开始IP比结束IP大则翻转他们的起始和结束
            if (beginNumber > endNumber) {
                ipRange.setBegin(end).setEnd(begin).setBeginNumber(endNumber).setEndNumber(beginNumber);
            } else {
                ipRange.setBegin(begin).setEnd(end).setBeginNumber(beginNumber).setEndNumber(endNumber);
            }
            ipRangeRepository.save(ipRange);
        }
    }

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


    @Override
    public OrgDTO findOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip) {
        List<Org> orgs = orgRepository.findAll(OrgRepository.SpecificationBuilder.findOrg(orgName, flag, spisFlag, eduFlag, ip, false));
        OrgDTO orgDTO = new OrgDTO();
        orgs.stream().findFirst().ifPresent(org -> {
            BeanUtil.copyProperties(org, orgDTO);
            List<IpRange> ipRanges = ipRangeRepository.findByOrgId(org.getId());
            List<IpRangeDTO> ipRangeDTOS = new ArrayList<>();
            ipRanges.forEach(ipRange -> {
                IpRangeDTO ipRangeDTO = new IpRangeDTO();
                ipRangeDTO.setBegin(ipRange.getBegin()).setEnd(ipRange.getEnd());
                ipRangeDTOS.add(ipRangeDTO);
            });
            orgDTO.setIpRanges(ipRangeDTOS);
        });
        return orgDTO;
    }

    @Override
    public Page<OrgDTO> likeOrg(String orgName, String flag, String spisFlag, String eduFlag, String ip, Pageable pageable) {
        Page<Org> orgPage = orgRepository.findAll(OrgRepository.SpecificationBuilder.findOrg(orgName, flag, spisFlag, eduFlag, ip, true), pageable);
        return null;
    }

    @Override
    public OrgDTO addOrg() {
        return null;
    }

    @Override
    public OrgDTO getOrg(Long orgId) {
        return null;
    }

    @Override
    public List<DepartmentDTO> findByOrgId(Long orgId) {
        List<DepartmentDTO> arrayDepartmentDTO = new ArrayList<>();
        List<Department> departments = departmentRepository.findByOrgId(orgId);
        Org byId = orgRepository.findById(orgId).orElse(null);
        for (Department department : departments){
            DepartmentDTO giveRecordDTO = new DepartmentDTO();
            giveRecordDTO.setOrgName(byId.getName());
            giveRecordDTO.setUserCount(0);
            BeanUtil.copyProperties(department, giveRecordDTO);
            arrayDepartmentDTO.add(giveRecordDTO);
        }
        return arrayDepartmentDTO;
    }

    @Override
    public Org getOrgId(Long orgId) {
        Org org = orgRepository.findById(orgId).orElse(null);
        return org;
    }

    @Override
    public Department insertDepartment(Long orgId, String name) {
        Department department = new Department();
        department.setName(name);
        department.setOrgId(orgId);
        departmentRepository.save(department);
        return department;
    }

    @Override
    public Department updateDepartment(Long id,Long orgId, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setOrgId(orgId);
        departmentRepository.save(department);
        return department;
    }

    @Override
    public void deleteDepartmentId(Long id) {
        departmentRepository.deleteById(id);

    }


    @Override
    public Page<OrgDTO> findByNameAndIp(Pageable pageable, String orgName, String ip) {
        Page<Org> org = orgRepository.findAll(OrgRepository.SpecificationBuilder.findByNameAndIp(orgName, ip), pageable);
        return coversOrgDTO(org);
    }

    @Override
    public Page<OrgDTO> findByStatus(Integer status, Pageable pageable) {
        Page<Org> org = orgRepository.findByStatus(status, pageable);
        return getOrgDTOS(status, org);
    }

    @Override
    public Page<OrgDTO> notFindByStatus(Pageable pageable) {
        Page<Org> org = orgRepository.notFindByStatus(pageable);
        return notGetOrgDTOS(org);
    }


    @Override
    public OrgDTO findByOrgNameDetail(Long id) {
        Org org = orgRepository.findById(id).orElse(null);
        OrgDTO orgDTO = new OrgDTO();
        List<IpRange> ipRanges = ipRangeRepository.findByOrgId(id);
        List<IpRangeDTO> ipRangeDTOS = new ArrayList<>();

        List<OrgProduct> orgProducts = orgProductRepository.findByOrgId(org.getId());
        List<ProductDTO> productDTOS = new ArrayList<>();
        Linkman linkman = linkmanRepository.findByOrgId(org.getId());
        if (linkman!=null){
            orgDTO.setContactPerson(linkman.getName());
            orgDTO.setContact(linkman.getPhone());
            orgDTO.setEmail(linkman.getEmail());
        }



        for (OrgProduct orgProduct : orgProducts){
            Product product = productRepository.findById(orgProduct.getProductId()).orElse(null);
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(product.getName());
            productDTO.setStatus(orgProduct.getStatus());
            productDTO.setId(orgProduct.getId());
            productDTO.setUrl(product.getUrl());
            productDTO.setBeginTime(orgProduct.getBeginDate());
            productDTO.setEndTime(orgProduct.getEndDate());
            productDTO.setOrgId(orgProduct.getOrgId());
            productDTO.setProductId(orgProduct.getProductId());
            boolean single = orgProduct.isSingle();
            if (single == true){
                productDTO.setSingle(true);
            }else{
                productDTO.setSingle(false);
            }

            productDTOS.add(productDTO);
            BeanUtil.copyProperties(org, orgDTO);
        }
        for (IpRange ipRange : ipRanges){
            IpRangeDTO ipRangeDTO = new IpRangeDTO();
            ipRangeDTO.setBegin(ipRange.getBegin());
            ipRangeDTO.setEnd(ipRange.getEnd());
            ipRangeDTOS.add(ipRangeDTO);
            BeanUtil.copyProperties(org, orgDTO);
        }
        orgDTO.setProducts(productDTOS);
        orgDTO.setIpRanges(ipRangeDTOS);
        return orgDTO;
    }

    @Override
    public void updateOrgAndLinkman(Long id, String orgName, String flag, String province, String city, String name, String email, String phone) {
        Org org = orgRepository.findById(id).orElse(null);
        org.setId(id);
        org.setName(orgName);
        org.setFlag(flag);
        org.setProvince(province);
        org.setCity(city);
        orgRepository.save(org);
        Linkman linkman = new Linkman();
        Linkman linkmans = linkmanRepository.findByOrgId(org.getId());
        if (linkmans!=null){
            Long linkmanId = linkmans.getId();
            linkman.setId(linkmanId);
        }
        linkman.setName(name);
        linkman.setPhone(phone);
        linkman.setEmail(email);
        linkman.setOrgId(org.getId());
        linkmanRepository.save(linkman);

    }

    @Override
    public void insertOrgAndProduct(Long orgId, Long productId, Date beginDate, Date endDate, Integer status, Boolean single) {
        OrgProduct orgProduct = new OrgProduct();
        orgProduct.setOrgId(orgId);
        orgProduct.setProductId(productId);
        orgProduct.setBeginDate(beginDate);
        orgProduct.setEndDate(endDate);
        orgProduct.setStatus(status);
        orgProduct.setSingle(single);
        orgProductRepository.save(orgProduct);
    }

    @Override
    public void insertOrgAndIpRangeS(Long orgId, String beginAndEnd) {

        String[] sourceStrArray = beginAndEnd.split(";");
        for (int i = 0; i < sourceStrArray.length; i++) {
            String ipBeginAndEnd = sourceStrArray[i];
            String[] sourceArray = ipBeginAndEnd.split("---");
            IpRange ipRange = new IpRange();

            ipRange.setOrgId(orgId);
            ipRange.setBegin(sourceArray[0]);
            long beginNumber = cn.hutool.core.util.NetUtil.ipv4ToLong(ipRange.getBegin());
            ipRange.setBeginNumber(beginNumber);

            ipRange.setEnd(sourceArray[1]);
            long endNumber = cn.hutool.core.util.NetUtil.ipv4ToLong(ipRange.getEnd());
            ipRange.setEndNumber(endNumber);

            ipRangeRepository.save(ipRange);

        }

    }


    @Override
    public Org insertOrgAndLinkman(String orgName, String flag, String province, String city, String name, String email, String phone) {
        Org org = new Org();
        org.setName(orgName);
        org.setFlag(flag);
        org.setProvince(province);
        org.setCity(city);
        orgRepository.save(org);
        if(email !=null || phone!=null || name !=null){
            Linkman linkman = new Linkman();
            linkman.setName(name);
            linkman.setPhone(phone);
            linkman.setEmail(email);
            linkman.setOrgId(org.getId());
            linkmanRepository.save(linkman);
        }
        return org;
    }

    @Override
    public void deleteIpRangeOrgId(Long orgId) {
        ipRangeRepository.deleteByOrgId(orgId);
    }

    @Override
    public void deleteProductOrgId(Long orgId) {
        orgProductRepository.deleteByOrgId(orgId);
    }

    @Override
    public Boolean findIpRangesExist(String begin, String end) {
        IpRange ipRange = ipRangeRepository.findByBeginAndEnd( begin, end);
        if (ipRange!=null){
            return true;
        }
        return false;
    }

    @Override
    public Boolean findExistsFlag(String flag) {
        Org org = orgRepository.findByFlag(flag);
        if (org!=null){
            return true;
        }
        return false;
    }

    @Override
    public Boolean findOrgNameExist(String name) {
        Org org = orgRepository.findByName(name);
        if (org!=null){
            return true;
        }
        return false;
    }

    @Override
    public Org findByName(String name) {
        Org org = orgRepository.findByName(name);
        return org;
    }




    private Page<OrgDTO> coversOrgDTO(Page<Org> orgRecordPage) {
        return orgRecordPage.map(org -> {
            OrgDTO orgDTO = new OrgDTO();
            List<OrgProduct> orgProducts = orgProductRepository.findByOrgId(org.getId());
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (OrgProduct orgProduct : orgProducts){
                Product product = productRepository.findById(orgProduct.getProductId()).orElse(null);
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setStatus(orgProduct.getStatus());
                productDTO.setBeginTime(orgProduct.getBeginDate());
                productDTO.setEndTime(orgProduct.getEndDate());
                productDTOS.add(productDTO);
                BeanUtil.copyProperties(org, orgDTO);
            }
            orgDTO.setProducts(productDTOS);
            return orgDTO;
        });
    }

    private Page<OrgDTO> getOrgDTOS(Integer status, Page<Org> org1) {
        return org1.map(org -> {
            OrgDTO orgDTO = new OrgDTO();
            List<OrgProduct> orgProducts = orgProductRepository.findByOrgIdAndStatus(org.getId(),status);
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (OrgProduct orgProduct : orgProducts){
                Product product = productRepository.findById(orgProduct.getProductId()).orElse(null);
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setStatus(orgProduct.getStatus());
                productDTO.setBeginTime(orgProduct.getBeginDate());
                productDTO.setEndTime(orgProduct.getEndDate());
                productDTOS.add(productDTO);
                BeanUtil.copyProperties(org, orgDTO);
            }
            orgDTO.setProducts(productDTOS);
            return orgDTO;
        });
    }

    private Page<OrgDTO> notGetOrgDTOS(Page<Org> org1) {
        return org1.map(org -> {
            OrgDTO orgDTO = new OrgDTO();
            List<OrgProduct> orgProducts = orgProductRepository.notFindByOrgId(org.getId());
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (OrgProduct orgProduct : orgProducts){
                Product product = productRepository.findById(orgProduct.getProductId()).orElse(null);
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setStatus(orgProduct.getStatus());
                productDTO.setBeginTime(orgProduct.getBeginDate());
                productDTO.setEndTime(orgProduct.getEndDate());
                productDTOS.add(productDTO);
                BeanUtil.copyProperties(org, orgDTO);
            }
            orgDTO.setProducts(productDTOS);
            return orgDTO;
        });
    }





}
