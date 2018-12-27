package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.dto.LiteratureDTO;
import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2018/5/8
 * @Description:
 */
public interface BackendService {


    /**
     * 获取互助列表
     *
     * @return
     */
    Page<HelpRecord> getHelpList(Pageable pageable, Map<String, Object> param);

    Page<LiteratureDTO> getLiteratureList(Pageable pageable, Map<String, Object> param);

    List<DocFile> getDocFileList(Pageable pageable, Long literatureId);

    DocFile saveDocFile(Literature literature, String fileId, String fileName);

    void give(Long id, Long giverId, String giverName, MultipartFile file);

    void third(Long id, Long giverId, String giverName);

    void failed(Long id, Long giverId, String giverName);

    void auditPass(Long id, Long auditorId, String auditorName);

    void auditNoPass(Long id, Long auditorId, String auditorName);

    /**
     * 获取单条可处理的记录
     *
     * @param id
     * @return
     */
    HelpRecord getWaitOrThirdHelpRecord(Long id);

    /**
     * 获取待审核的求助记录
     *
     * @param id
     * @return
     */
    HelpRecord getWaitAuditHelpRecord(Long id);

    /**
     * 查询待审核的
     *
     * @param giveRecordId
     * @return
     */
    GiveRecord getWaitAudit(Long giveRecordId);

    /**
     * 根据helpRecord获取giverRecord
     *
     * @param helpRecordId
     */
    GiveRecord getGiverRecord(Long helpRecordId, int auditStatus, int giverType);

    /**
     * 复用、取消复用
     *
     * @return
     */
    boolean reusing(Map<String, Object> param);

}
