package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjDaySetting;
import com.wd.cloud.wdtjserver.entity.TjHisSetting;
import com.wd.cloud.wdtjserver.entity.TjOrg;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjService {
    //查询学校
    List<TjOrg> likeOrgName(String orgName);

    TjOrg save(TjOrg tjOrg);

    TjDaySetting save(TjDaySetting tjDaySetting);

    TjHisSetting save(TjHisSetting tjHisSetting);


    //按时查询
    List<Map<String,Object>> findByTjDateAndOrgIdTime(Date beginDate,Date endDate,long orgId);
    //按天查询
    List<Map<String,Object>> findByTjDateAndOrgIdDay(Date beginDate,Date endDate,long orgId);

    //按月查询
    List<Map<String,Object>> findByTjDateAndOrgIdMonth(Date beginDate,Date endDate,long orgId);

    //按年查询
    List<Map<String,Object>> findByTjDateAndOrgIdYear(Date beginDate, Date endDate, long orgId);



}
