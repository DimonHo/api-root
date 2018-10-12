package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.model.DownloadModel;

/**
 * @author He Zhigang
 * @date 2018/5/16
 * @Description:
 */
public interface FileService {

    DownloadModel getDownloadFile(Long helpRecordId);

    DownloadModel getWaitAuditFile(Long helpRecordId);

    String getDownloadUrl(Long helpRecordId);
}
