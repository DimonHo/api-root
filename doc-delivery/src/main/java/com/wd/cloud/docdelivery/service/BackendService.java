package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Literature> getLiteratureList(Pageable pageable, Map<String, Object> param);

    List<DocFile> getDocFileList(Pageable pageable, Long literatureId);

    DocFile saveDocFile(Literature literature, String fileName);

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
     * 更新互助记录
     *
     * @param helpRecord
     */
    void updateHelRecord(HelpRecord helpRecord);

    /**
     * 根据helpRecord获取giverRecord
     *
     * @param helpRecord
     */
    public GiveRecord getGiverRecord(HelpRecord helpRecord, int auditStatus, int giverType);


    public void saveGiveRecord(GiveRecord giveRecord);

    /**
     * 复用、取消复用
     *
     * @return
     */
    public boolean reusing(Map<String, Object> param);

}
