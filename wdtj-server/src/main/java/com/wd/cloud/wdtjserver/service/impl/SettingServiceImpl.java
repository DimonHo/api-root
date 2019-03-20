package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.feign.UoServerApi;
import com.wd.cloud.wdtjserver.repository.TjHisQuotaRepository;
import com.wd.cloud.wdtjserver.repository.TjOrgRepository;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.service.SettingService;
import com.wd.cloud.wdtjserver.utils.JpaQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private static final Log log = LogFactory.get();
    @Autowired
    TjOrgRepository tjOrgRepository;

    @Autowired
    TjQuotaRepository tjQuotaRepository;

    @Autowired
    TjHisQuotaRepository tjHisQuotaRepository;

    @Autowired
    UoServerApi uoServerApi;

    @Override
    public TjOrg save(TjOrg tjOrg) {
        ResponseModel responseModel = uoServerApi.getOrg(tjOrg.getOrgFlag());
        String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
        if (!responseModel.isError()) {
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgFlagAndHistoryIsFalse(tjOrg.getOrgFlag());
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                tjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            tjOrg.setOrgName(orgName);
            TjQuota tjQuota = tjQuotaRepository.findByOrgFlagAndHistoryIsFalse(tjOrg.getOrgFlag());
            if (tjQuota == null) {
                tjQuota = new TjQuota();
                tjQuotaRepository.save(tjQuota);
            }
            return tjOrgRepository.save(tjOrg);
        }
        log.error("机构管理服务调用失败：{}", responseModel.getMessage());
        return null;

    }

    @Override
    public TjOrg getOrgInfo(String orgFlag) {
        return tjOrgRepository.findByOrgFlagAndHistoryIsFalse(orgFlag);
    }

    @Override
    public TjOrg saveTjOrg(String orgFlag, boolean showPv, boolean showSc, boolean showDc, boolean showDdc, boolean showAvgTime, String createUser) {
        ResponseModel responseModel = uoServerApi.getOrg(orgFlag);
        String orgName = JSONUtil.parseObj(responseModel.getBody(), true).getStr("name");
        if (!responseModel.isError()) {
            //根据学校ID查询是否有该学校
            TjOrg oldTjOrg = tjOrgRepository.findByOrgFlagAndHistoryIsFalse(orgFlag);
            TjOrg newTjOrg = new TjOrg();
            if (oldTjOrg != null) {
                //修改History为true
                oldTjOrg.setHistory(true);
                newTjOrg.setPid(oldTjOrg.getId());
                tjOrgRepository.save(oldTjOrg);
            }
            newTjOrg.setOrgFlag(orgFlag)
                    .setOrgName(orgName)
                    .setCreateUser(createUser)
                    .setShowPv(showPv)
                    .setShowSc(showSc)
                    .setShowDc(showDc)
                    .setShowDdc(showDdc)
                    .setShowAvgTime(showAvgTime);

            TjQuota tjQuota = tjQuotaRepository.findByOrgFlagAndHistoryIsFalse(orgFlag);
            if (tjQuota == null) {
                tjQuota = new TjQuota();
                tjQuota.setOrgFlag(orgFlag);
                tjQuota.setOrgName(orgName);
                tjQuotaRepository.save(tjQuota);
            }

            return tjOrgRepository.save(newTjOrg);
        }
        return null;
    }

    @Override
    public TjOrg forbade(String orgFlag) {
        TjOrg tjOrg = tjOrgRepository.findByOrgFlagAndHistoryIsFalse(orgFlag);
        if (tjOrg != null) {
            // 禁用和解除禁用切换
            tjOrg.setForbade(!tjOrg.isForbade());
            tjOrg = tjOrgRepository.save(tjOrg);
        }
        return tjOrg;
    }


    @Override
    public Page<TjOrg> likeQuery(String query, Boolean history, Pageable pageable) {
        Specification<TjOrg> specification = JpaQueryUtil.buildLikeQuery(query, history);

        return tjOrgRepository.findAll(specification, pageable);

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
        Specification<TjOrg> specification = JpaQueryUtil.buildFilterForTjOrg(showPv, showSc, showDc, showDdc, showAvgTime, forbade);
        return tjOrgRepository.findAll(specification, pageable);
    }

    @Override
    public List<JSONObject> getOrgList() {
        List<JSONObject> orgs = new ArrayList<>();
        ResponseModel<List<JSONObject>> responseModel = uoServerApi.getAll();
        List<String> orgNames = tjOrgRepository.distinctByOrgFlag();
        if (!responseModel.isError()) {
            log.info("总机构数量：{}", responseModel.getBody().size());
            responseModel.getBody().stream().filter(o -> !orgNames.contains(o.getStr("name")))
                    .forEach(orgInfo -> orgs.add(orgInfo));
        }
        log.info("可添加机构数量：{}", orgs.size());
        return orgs;
    }


}
