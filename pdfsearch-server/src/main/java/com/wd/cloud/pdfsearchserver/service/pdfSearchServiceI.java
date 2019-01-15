package com.wd.cloud.pdfsearchserver.service;

import com.wd.cloud.commons.model.ResponseModel;

import java.util.Map;

public interface pdfSearchServiceI {
    public ResponseModel<byte[]> getpdf(Map<String, Object> map);
}
