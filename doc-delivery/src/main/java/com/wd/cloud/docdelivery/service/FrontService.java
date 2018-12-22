package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface FrontService {

    boolean checkExists(String email, Literature literature);

    DocFile saveDocFile(Literature literature, String fileId,String filaName);

    /**
     * 我要应助
     *
     * @param helpRecordId
     * @param giverId
     * @param giverName
     */
    HelpRecord givingHelp(long helpRecordId, long giverId, String giverName, String giverIp);

    boolean cancelGivingHelp(long helpRecordId, long giverId);

    /**
     * 得到应种中状态的应助记录
     *
     * @param helpRecordId
     * @return
     */
    HelpRecord getHelpingRecord(long helpRecordId);

    /**
     * 获取用户今天的求助次数
     *
     * @param email
     * @return
     */
    int getCountHelpRecordToDay(String email);

    /**
     * 获取单条可应助的记录
     *
     * @param id
     * @return
     */
    HelpRecord getWaitOrThirdHelpRecord(Long id);

    /**
     * 获取非待应助的求助记录
     *
     * @param helpRecordId
     * @return
     */
    HelpRecord getNotWaitRecord(long helpRecordId);

    /**
     * 去除字符串中的HTML标签
     *
     * @param docTitle
     * @return
     */
    String clearHtml(String docTitle);

    /**
     * 保存元数据
     *
     * @param literature
     * @return
     */
    Literature saveLiterature(Literature literature);

    /**
     * 保存应助记录
     *
     * @param giveRecord
     * @return
     */
    GiveRecord saveGiveRecord(GiveRecord giveRecord);

    /**
     * 创建应助记录
     */
    void createGiveRecord(HelpRecord helpRecord, long giveUserId, DocFile docFile, String giviIp);

    /**
     * 保存求助记录
     *
     * @param helpRecord
     * @return
     */
    HelpRecord saveHelpRecord(HelpRecord helpRecord);

    HelpRecord getHelpRecord(long helpRecordId);

    /**
     * 查询元数据
     *
     * @param literature
     * @return
     */
    Literature queryLiterature(Literature literature);

    /**
     * 获取用户的求助记录
     *
     * @param helpUserId
     * @return
     */
    Page<HelpRecord> getHelpRecordsForUser(long helpUserId,int status, Pageable pageable);

    /**
     * 获取用户的求助记录
     *
     * @param helpEmail
     * @return
     */
    Page<HelpRecord> getHelpRecordsForEmail(String helpEmail, Pageable pageable);

    /**
     * 获取待应助的求助记录
     *
     * @return
     */
    Page<HelpRecord> getWaitHelpRecords(int helpChannel, Pageable pageable);

    /**
     * 求助完成列表
     *
     * @param helpChannel
     * @param pageable
     * @return
     */
    Page<HelpRecord> getFinishHelpRecords(int helpChannel, Pageable pageable);

    /**
     * 求助成功列表
     * @param helpChannel
     * @param pageable
     * @return
     */
    Page<HelpRecord> getSuccessHelpRecords(int helpChannel, Pageable pageable);

    /**
     * 疑难文献（无结果，求助失败）列表
     * @param helpChannel
     * @param pageable
     * @return
     */
    Page<HelpRecord> getFailedHelpRecords(int helpChannel, Pageable pageable);

    Page<HelpRecord> getAllHelpRecord(Pageable pageable);

    DocFile getReusingFile(Literature literature);

    Page<HelpRecord> search(String keyword, Pageable pageable);

    String checkExistsGiveing(long giverId);

}
