package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.dto.LiteratureDTO;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
public interface ProcessService {

    /**
     * 等待第三方应助
     *
     * @param helpRecordId
     * @param giverId
     * @param giverName
     */
    void third(Long helpRecordId, Long giverId, String giverName);

    /**
     * 直接应助成功
     *
     * @param helpRecordId
     * @param giverId
     * @param giverName
     * @param file
     */
    void give(Long helpRecordId, Long giverId, String giverName, MultipartFile file);

    /**
     * 标记疑难文献
     *
     * @param helpRecordId
     * @param giverId
     * @param giverName
     */
    void failed(Long helpRecordId, Long giverId, String giverName);

    Page<HelpRecordDTO> waitHelpRecordList(Pageable pageable);
    Page<HelpRecordDTO> successHelpRecordList(Pageable pageable);
    Page<HelpRecordDTO> failedHelpRecordList(Pageable pageable);
    Page<HelpRecordDTO> waitAuditHelpRecordList(Pageable pageable);
    Page<HelpRecordDTO> helpingHelpRecordList(Pageable pageable);

    Page<HelpRecordDTO> helpRecordList(Map<String, Object> query, Pageable pageable);

    Page<LiteratureDTO> literatureList(Map<String, Object> query, Pageable pageable);
}
