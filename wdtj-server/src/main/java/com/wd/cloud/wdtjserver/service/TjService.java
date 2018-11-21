package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.entity.TjHisQuota;
import com.wd.cloud.wdtjserver.entity.TjOrg;
import com.wd.cloud.wdtjserver.entity.TjQuota;
import com.wd.cloud.wdtjserver.entity.TjViewData;
import com.wd.cloud.wdtjserver.model.DateIntervalModel;
import com.wd.cloud.wdtjserver.model.HisQuotaModel;
import com.wd.cloud.wdtjserver.model.ViewDataModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface TjService {




    /**
     * @param orgId
     * @param beginTime
     * @param entTime
     * @return
     */
    ViewDataModel getViewDate(Long orgId, String beginTime, String entTime, int viewType);

}
