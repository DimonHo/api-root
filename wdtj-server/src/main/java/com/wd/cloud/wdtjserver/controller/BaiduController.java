package com.wd.cloud.wdtjserver.controller;

import com.google.common.net.MediaType;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.wdtjserver.config.BaiduTjConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author He Zhigang
 * @date 2018/11/1
 * @Description:
 */
public class BaiduController {

    @Autowired
    BaiduTjConfig baiduTjConfig;

    public ResponseModel pv(){
        return null;
    }
}
