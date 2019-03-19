package com.wd.cloud.pdfsearchserver.service;

import com.alibaba.fastjson.JSON;
import com.wd.cloud.commons.model.ResponseModel;

import java.util.List;

public interface LibgenAllSearchServiceI {
    public ResponseModel<List<JSON>> getResult(String value);
}
