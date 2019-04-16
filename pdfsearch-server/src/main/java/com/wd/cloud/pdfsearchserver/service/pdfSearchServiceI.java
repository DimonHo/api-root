package com.wd.cloud.pdfsearchserver.service;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.pdfsearchserver.model.LiteratureModel;

public interface pdfSearchServiceI {
    public ResponseModel<String> getRowKey(LiteratureModel literatureModel);

    public ResponseModel<byte[]> getpdf(String rowKey);
}
