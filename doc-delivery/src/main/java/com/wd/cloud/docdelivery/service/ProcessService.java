package com.wd.cloud.docdelivery.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
public interface ProcessService {

    void third(Long helpRecordId,Long giverId,String giverName);

    void give(Long helpRecordId, Long giverId, String giverName, MultipartFile file);

    void notFound(Long helpRecordId, Long giverId, String giverName);
}
