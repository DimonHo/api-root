package com.wd.cloud.userserver.service.impl;

import com.wd.cloud.userserver.entity.AuditIdCard;
import com.wd.cloud.userserver.entity.UserInfo;
import com.wd.cloud.userserver.enums.AuditIdCardEnums;
import com.wd.cloud.userserver.repository.AuditIdCardRepository;
import com.wd.cloud.userserver.repository.UserInfoRepository;
import com.wd.cloud.userserver.service.AuditIdCardServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;


@Slf4j
@Service("auditIdCardService")
@Transactional(rollbackFor = Exception.class)
public class AuditIdCardServerImpl implements AuditIdCardServer {


    @Autowired
    AuditIdCardRepository auditIdCardRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    public void give(String userName, String idPhoto, String nickName, String orgName, String department, Integer identity,
                     String departmentId, Integer education, Short sex, String entranceTime,String email,Integer permission) {
        AuditIdCard auditIdCard = new AuditIdCard();
        auditIdCard.setUsername(userName);
        auditIdCard.setIdPhoto(idPhoto);
        auditIdCard.setNickName(nickName);
        auditIdCard.setOrgName(orgName);
        auditIdCard.setStatus(AuditIdCardEnums.AUDIE_AUDIT.value());
        auditIdCard.setDepartment(department);
        auditIdCard.setDepartmentId(departmentId);
        auditIdCard.setIdentity(identity);
        auditIdCard.setEducation(education);
        auditIdCard.setSex(sex);
        auditIdCard.setEntranceTime(entranceTime);
        auditIdCard.setEmail(email);
        auditIdCard.setPermission(permission);
        auditIdCardRepository.save(auditIdCard);
    }

    @Override
    public AuditIdCard getUserName(String userName) {
        AuditIdCard auditIdCard = auditIdCardRepository.findByUsername(userName).orElse(null);
        return auditIdCard;
    }

    @Override
    public Page<AuditIdCard> findAll(Pageable pageable, Map<String, Object> param) {
        Integer status = (Integer) param.get("status");
        String keyword = ((String) param.get("keyword"));
        keyword = keyword != null ? keyword.replaceAll("\\\\", "\\\\\\\\") : null;
        Page<AuditIdCard> result = auditIdCardRepository.findAll(AuditIdCardRepository.SpecificationBuilder.buildBackendList(status,keyword), pageable);
        return result;
    }

    @Override
    public AuditIdCard findById(Long id) {
        AuditIdCard idCard = auditIdCardRepository.findById(id).orElse(null);
        return idCard;
    }

    @Override
    public void apply(Long id,Integer permission,String handlerName) {
        AuditIdCard idCard = auditIdCardRepository.findById(id).orElse(null);
        Date date = new Date();
        idCard.setHandleTime(date);
        idCard.setHandlerName(handlerName);
        idCard.setPermission(permission);
        idCard.setStatus(2);
        auditIdCardRepository.save(idCard);

        UserInfo userInfo = userInfoRepository.findByUsername(idCard.getUsername()).orElse(null);
        userInfo.setIdPhoto(idCard.getIdPhoto());
        userInfo.setValidated(true);
        userInfo.setNickname(idCard.getNickName());
        userInfo.setDepartment(idCard.getDepartment());
        userInfo.setDepartmentId(idCard.getDepartmentId());
        userInfo.setPermission(permission);
        userInfo.setSex(idCard.getSex());
        userInfo.setIdentity(idCard.getIdentity());
        userInfo.setEducation(idCard.getEducation());
        userInfo.setEntranceTime(idCard.getEntranceTime());
        userInfoRepository.save(userInfo);
    }

    @Override
    public void notApply(Long id,Integer permission,String handlerName) {
        AuditIdCard idCard = auditIdCardRepository.findById(id).orElse(null);
        Date date = new Date();
        idCard.setHandleTime(date);
        idCard.setHandlerName(handlerName);
        idCard.setPermission(permission);
        idCard.setStatus(1);
        auditIdCardRepository.save(idCard);
    }
}
