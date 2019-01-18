package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.dto.GiveRecordDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
public interface FrontService {

    boolean checkExists(String email, Long literatureId);

    DocFile saveDocFile(Long literatureId, String fileId, String filaName);

    String help(HelpRecord helpRecord,Literature literature);

    HelpRecord give(Long helpRecordId, Long giverId, String giverName, String ip);

    void uploadFile(HelpRecord helpRecord, Long giverId, MultipartFile file, String ip);

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
     * 创建应助记录
     */
    void createGiveRecord(HelpRecord helpRecord, long giveUserId, DocFile docFile, String giviIp);

    /**
     * 获取用户的求助记录
     *
     * @param status
     * @return
     */
    Page<HelpRecordDTO> myHelpRecords(List<Integer> status, Pageable pageable);

    Page<GiveRecordDTO> myGiveRecords(List<Integer> status, Pageable pageable);


    Page<HelpRecordDTO> getHelpRecords(List<Integer> channel, List<Integer> status, String email, String keyword, Pageable pageable);

    /**
     * 获取待应助的求助记录
     *
     * @return
     */
    Page<HelpRecordDTO> getWaitHelpRecords(List<Integer> channel, Pageable pageable);

    /**
     * 求助完成列表
     *
     * @param channel
     * @param pageable
     * @return
     */
    Page<HelpRecordDTO> getFinishHelpRecords(List<Integer> channel, Pageable pageable);

    /**
     * 求助成功列表
     *
     * @param helpChannel
     * @param pageable
     * @return
     */
    Page<HelpRecordDTO> getSuccessHelpRecords(List<Integer> helpChannel, Pageable pageable);

    /**
     * 疑难文献（无结果，求助失败）列表
     *
     * @param helpChannel
     * @param pageable
     * @return
     */
    Page<HelpRecordDTO> getFailedHelpRecords(List<Integer> helpChannel, Pageable pageable);

    DocFile getReusingFile(Long literatureId);

    String checkExistsGiveing(long giverId);

    Permission getOrgIdAndRule(Long orgId, int rule);

    Permission getByLevelAndOrgId(int level,long orgId);

    int findByHelperEmailCount(String helperEmail);

    int findByHelperEmailDay(String helperEmail);

}
