package com.wd.cloud.docdelivery.service;

import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.docdelivery.dto.GiveRecordDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.*;
import org.hibernate.exception.ConstraintViolationException;
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

    String help(HelpRecord helpRecord,Literature literature) throws ConstraintViolationException;

    void give(Long helpRecordId, String giverName, String ip);

    void uploadFile(HelpRecord helpRecord, String giverName, MultipartFile file, String ip);

    /**
     * 我要应助
     *
     * @param helpRecordId
     * @param giverName
     */
    HelpRecord givingHelp(long helpRecordId, String giverName, String giverIp);

    boolean cancelGivingHelp(long helpRecordId, String giverName);

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
    Long getCountHelpRecordToDay(String email);

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
    void createGiveRecord(HelpRecord helpRecord, String giverName, DocFile docFile, String giviIp);

    /**
     * 获取用户的求助记录
     *
     * @param status
     * @return
     */
    Page<HelpRecordDTO> myHelpRecords(String helperName, List<Integer> status, Pageable pageable);

    Page<GiveRecordDTO> myGiveRecords(String giverName, List<Integer> status, Pageable pageable);


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

    Permission getPermission(Long orgId, Integer level);

    Permission nextPermission(Long orgId, Integer level);

}
