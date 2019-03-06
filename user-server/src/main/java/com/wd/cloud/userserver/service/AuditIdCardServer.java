package com.wd.cloud.userserver.service;

import com.wd.cloud.userserver.entity.AuditIdCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;


public interface AuditIdCardServer {
    void give(String userName,String idPhoto,String nickName,String orgName,String department,Integer identity,
              String departmentId,Integer education,Short sex,String entranceTime,String email,Integer permission);


    AuditIdCard getUserName(String userName);

    Page<AuditIdCard> findAll(Pageable pageable, Map<String, Object> param);

    AuditIdCard findById(Long id);

    void apply(Long id,Integer permission,String handlerName);

    void notApply(Long id,Integer permission,String handlerName);
}
