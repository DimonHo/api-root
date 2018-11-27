package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.model.ViewDataModel;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface ViewService {


    /**
     * @param orgId
     * @param beginTime
     * @param entTime
     * @return
     */
    ViewDataModel getViewDate(Long orgId, String beginTime, String entTime, int viewType);

}
