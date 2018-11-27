package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjQuota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/11/20
 * @Description:
 */
public interface QuotaService {

    /**
     * 保存/更新 机构日基数设置
     *
     * @param tjQuota
     * @return
     */
    TjQuota save(TjQuota tjQuota);


    /**
     * @param orgId
     * @return
     */
    TjQuota findOrgQuota(Long orgId);

    /**
     * @param orgId
     * @return
     */
    Page<TjQuota> findOrgQuota(Long orgId, Boolean history, Pageable pageable);

    /**
     * @param history
     * @return
     */
    Page<TjQuota> findAll(Boolean history, Pageable pageable);

    /**
     * 模糊查询
     *
     * @param query
     * @return
     */
    Page<TjQuota> likeQuery(String query, Boolean history, Pageable pageable);

    void runTask(Date date);

}
