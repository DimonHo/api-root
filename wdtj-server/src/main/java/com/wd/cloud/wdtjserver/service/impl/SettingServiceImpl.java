package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.json.JSONUtil;
import com.netflix.discovery.converters.Auto;
import com.wd.cloud.apifeign.OrgServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.service.SettingService;
import com.wd.cloud.wdtjserver.utils.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/16
 * @Description:
 */
@Service("settingService")
@Transactional(rollbackFor = Exception.class)
public class SettingServiceImpl implements SettingService {

    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjQuotaRepository tjQuotaRepository;

    @Autowired
    TjHisQuotaRepository tjHisQuotaRepository;

    @Autowired
    OrgServerApi orgServerApi;

    @Override
    public TjOrg save(TjOrg tjOrg) {
        ResponseModel responseModel = orgServerApi.getOrg(tjOrg.getOrgId());
        String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
        if (!responseModel.isError()) {
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                tjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            tjOrg.setOrgName(orgName);
            return tjOrgRepository.save(tjOrg);
        }
        return null;

    }

    @Override
    public TjOrg saveTjOrg(long orgId, boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime) {
        ResponseModel responseModel = orgServerApi.getOrg(orgId);
        String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
        if (!responseModel.isError()) {
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(orgId);
            TjOrg newTjOrg = new TjOrg();
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                newTjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            newTjOrg.setOrgName(orgName)
                    .setShowPv(showPv)
                    .setShowSc(showSc)
                    .setShowDc(showDc)
                    .setShowDdc(showDdc)
                    .setShowAvgTime(showAvgTime);
            return tjOrgRepository.save(newTjOrg);
        }
        return null;
    }

    @Override
    public TjOrg forbade(Long orgId) {
        TjOrg tjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(orgId);
        if (tjOrg != null){
            // 禁用和解除禁用切换
            tjOrg.setForbade(!tjOrg.isForbade());
            tjOrg = tjOrgRepository.save(tjOrg);
        }
        return tjOrg;
    }


    @Override
    public TjQuota save(TjQuota tjQuota) {
        //根据学校ID查询TjDaySetting是否有数据
        TjQuota oldTjQuota = tjQuotaRepository.findByOrgIdAndHistoryIsFalse(tjQuota.getOrgId());
        if (oldTjQuota != null) {
            oldTjQuota.setHistory(true);
            tjQuota.setPid(oldTjQuota.getId());
            tjQuotaRepository.save(oldTjQuota);
        }
        return tjQuotaRepository.save(tjQuota);
    }

    @Override
    public TjHisQuota save(TjHisQuota tjHisQuota) {
        return tjHisQuotaRepository.save(tjHisQuota);
    }

    @Override
    public List<TjHisQuota> save(List<TjHisQuota> tjHisQuotas) {
        return tjHisQuotaRepository.saveAll(tjHisQuotas);
    }


}
