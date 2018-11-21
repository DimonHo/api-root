package com.wd.cloud.wdtjserver.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.repository.TjQuotaRepository;
import com.wd.cloud.wdtjserver.service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
@Service("quotaService")
@Transactional(rollbackFor = Exception.class)
public class QuotaServiceImpl implements QuotaService {
    private static final Log log = LogFactory.get();
    @Autowired
    TjQuotaRepository tjQuotaRepository;

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
    public TjQuota findOrgQuota(Long orgId) {
        return tjQuotaRepository.findByOrgIdAndHistoryIsFalse(orgId);
    }

    @Override
    public Page<TjQuota> findOrgQuota(Long orgId, Boolean history, Pageable pageable) {
        if (history == null) {
            return tjQuotaRepository.findByOrgId(orgId, pageable);
        } else {
            return tjQuotaRepository.findByOrgIdAndHistory(orgId, history, pageable);
        }

    }

    @Override
    public Page<TjQuota> findAll(Boolean history, Pageable pageable) {
        if (history != null) {
            return tjQuotaRepository.findByHistory(history, pageable);
        }
        return tjQuotaRepository.findAll(pageable);
    }
}
