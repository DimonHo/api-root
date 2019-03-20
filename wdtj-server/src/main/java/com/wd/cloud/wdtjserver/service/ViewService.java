package com.wd.cloud.wdtjserver.service;

import com.wd.cloud.wdtjserver.model.ViewDataModel;


/**
 * @author He Zhigang
 * @date 2018/11/6
 * @Description:
 */
public interface ViewService {


    /**
     * @param orgFlag
     * @param beginTime
     * @param entTime
     * @return
     */
    ViewDataModel getViewDate(String orgFlag, String beginTime, String entTime, int viewType);

}
