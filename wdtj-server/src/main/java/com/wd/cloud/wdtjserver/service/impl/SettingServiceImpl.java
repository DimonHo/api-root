package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.feign.OrgServerApi;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.service.SettingService;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author He Zhigang
 * @date 2018/11/16
 * @Description:
 */
@Service("settingService")
@Transactional(rollbackFor = Exception.class)
public class SettingServiceImpl implements SettingService {
    private static final Log log = LogFactory.get();
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
            TjQuota tjQuota = tjQuotaRepository.findByOrgIdAndHistoryIsFalse(tjOrg.getOrgId());
            if (tjQuota == null) {
                tjQuota = new TjQuota();
                tjQuotaRepository.save(tjQuota);
            }
            return tjOrgRepository.save(tjOrg);
        }
        return null;

    }

    @Override
    public TjOrg saveTjOrg(long orgId, boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime, String createUser) {
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
            newTjOrg.setOrgId(orgId)
                    .setOrgName(orgName)
                    .setCreateUser(createUser)
                    .setShowPv(showPv)
                    .setShowSc(showSc)
                    .setShowDc(showDc)
                    .setShowDdc(showDdc)
                    .setShowAvgTime(showAvgTime);

            TjQuota tjQuota = tjQuotaRepository.findByOrgIdAndHistoryIsFalse(orgId);
            if (tjQuota == null) {
                tjQuota = new TjQuota();
                tjQuota.setOrgId(orgId);
                tjQuota.setOrgName(orgName);
                tjQuotaRepository.save(tjQuota);
            }

            return tjOrgRepository.save(newTjOrg);
        }
        return null;
    }

    @Override
    public TjOrg forbade(Long orgId) {
        TjOrg tjOrg = tjOrgRepository.findByOrgIdAndHistoryIsFalse(orgId);
        if (tjOrg != null) {
            // 禁用和解除禁用切换
            tjOrg.setForbade(!tjOrg.isForbade());
            tjOrg = tjOrgRepository.save(tjOrg);
        }
        return tjOrg;
    }


    @Override
    public Page<TjOrg> likeOrgName(String orgName, Boolean history, Pageable pageable) {
        return tjOrgRepository.findAll(JpaQueryUtil.buildQeuryForTjOrg(orgName, history), pageable);
    }

    @Override
    public Page<TjOrg> getEnabledFromAll(Pageable pageable) {
        return tjOrgRepository.findAllByHistory(false, pageable);
    }

    @Override
    public Page<TjOrg> getHistoryFromAll(Pageable pageable) {

        return tjOrgRepository.findAllByHistory(true, pageable);
    }

    @Override
    public Page<TjOrg> getAll(Pageable pageable) {
        return tjOrgRepository.findAll(pageable);
    }

    @Override
    public Page<TjOrg> filterOrgByQuota(Boolean showPv, Boolean showSc, Boolean showDc, Boolean showDdc, Boolean showAvgTime, Boolean forbade, Pageable pageable) {
        return tjOrgRepository.findAll(JpaQueryUtil.buildFilterForTjOrg(showPv, showSc, showDc, showDdc, showAvgTime, forbade), pageable);
    }


}
