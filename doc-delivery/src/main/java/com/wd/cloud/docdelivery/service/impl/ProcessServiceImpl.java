package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.ProcessException;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.MailService;
import com.wd.cloud.docdelivery.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author He Zhigang
 * @date 2018/12/26
 * @Description:
 */
@Slf4j
@Service("processService")
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    MailService mailService;

    @Autowired
    Global global;


    @Override
    public void third(Long helpRecordId, Long giverId, String giverName) {
        HelpRecord helpRecord = helpRecordRepository.findById(helpRecordId).orElse(null);
        if (helpRecord == null || helpRecord.getStatus() > HelpStatusEnum.WAIT_HELP.getCode()) {
            throw new ProcessException(HttpStatus.HTTP_INTERNAL_ERROR, "状态不合法");
        }
        helpRecord.setStatus(HelpStatusEnum.HELP_THIRD.getCode());
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setGiverId(giverId).setGiverName(giverName).setGiverType(GiveTypeEnum.THIRD.getCode());
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
        String docTitle = literature != null ? literature.getDocTitle() : "";
        mailService.sendMail(helpRecord);
        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);
    }

    @Override
    public void give(Long helpRecordId, Long giverId, String giverName, MultipartFile file) {
        HelpRecord helpRecord = helpRecordRepository.findById(helpRecordId).orElse(null);
        if (helpRecord == null || helpRecord.getStatus() >= HelpStatusEnum.HELP_SUCCESSED.getCode()) {
            throw new ProcessException(HttpStatus.HTTP_INTERNAL_ERROR, "该求助不存在或已完成");
        }
        String fileId = fileExists(file);
        fileId = fileId == null ? uploadFile(file) : fileId;
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
        DocFile docFile = new DocFile();
        docFile.setLiterature(literature).setFileId(fileId);
        //保存文件上传记录
        docFileRepository.save(docFile);
        //如果有求助第三方的状态的应助记录，则直接处理更新这个记录
        GiveRecord giveRecord = new GiveRecord();
        if (helpRecord.getStatus() == HelpStatusEnum.HELP_THIRD.getCode()) {
            giveRecord = giveRecordRepository.findByHelpRecordIdAndAuditStatusEquals(helpRecord.getId(), GiveTypeEnum.THIRD.getCode());
        }
        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecord.setDocFileId(docFile.getId());
        //设置应助类型为管理员应助
        giveRecord.setGiverType(GiveTypeEnum.MANAGER.getCode());
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverName(giverName);
        //修改求助状态为应助成功
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getCode());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);
        mailService.sendMail(helpRecord);
    }

    private String uploadFile(MultipartFile file) {
        ResponseModel<JSONObject> uploadResult = fsServerApi.uploadFile(global.getHbaseTableName(), file);
        if (uploadResult.isError()) {
            log.info("文件[file = {},size = {}] 上传失败 。。。", file.getOriginalFilename(), file.getSize());
            throw new FeignException("fsServer.uploadFile");
        }
        log.info("文件{}上传成功!", file.getOriginalFilename());
        return uploadResult.getBody().getStr("fileId");
    }

    private String fileExists(MultipartFile file) {
        try {
            String fileMd5 = FileUtil.fileMd5(file.getInputStream());
            ResponseModel<JSONObject> checkResult = fsServerApi.checkFile(global.getHbaseTableName(), fileMd5);
            if (!checkResult.isError() && checkResult.getBody() != null) {
                log.info("文件已存在，秒传成功！");
                return checkResult.getBody().getStr("fileId");
            }
        } catch (IOException e) {
            throw new ProcessException(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
        }
        return null;
    }

    @Override
    public void notFound(Long helpRecordId, Long giverId, String giverName) {

    }
}
