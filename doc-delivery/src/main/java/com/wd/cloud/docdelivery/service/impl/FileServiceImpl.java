package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.config.GlobalConfig;
import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.Literature;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.model.DownloadFileModel;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author He Zhigang
 * @date 2018/5/16
 * @Description:
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    private static final Log log = LogFactory.get();

    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Override
    public DownloadFileModel getDownloadFile(Long helpRecordId) {
        HelpRecord helpRecord = null;
        try {
            helpRecord = helpRecordRepository.getOne(helpRecordId);
            if (checkTimeOut(helpRecord.getGmtModified())) {
                return null;
            }
        } catch (Exception e) {
            log.error("未找到id为{}的数据", helpRecordId);
            return null;
        }
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordId(helpRecordId);
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    @Override
    public DownloadFileModel getWaitAuditFile(Long helpRecordId) {
        HelpRecord helpRecord = helpRecordRepository.getOne(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndAuditStatusEquals(helpRecordId, AuditEnum.WAIT.getCode());
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    private DownloadFileModel buildDownloadModel(HelpRecord helpRecord, GiveRecord giveRecord) {
        DocFile docFile = docFileRepository.findById(giveRecord.getDocFileId()).orElse(null);
        String fileId = docFile != null ? docFile.getFileId() : null;
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
        String docTitle = literature != null ? literature.getDocTitle() : null;
        //以文献标题作为文件名，标题中可能存在不符合系统文件命名规范，在这里规范一下。
        docTitle = FileUtil.cleanInvalid(docTitle);
        DownloadFileModel downloadFileModel = new DownloadFileModel();
        ResponseModel<byte[]> responseModel = fsServerApi.getFileByte(fileId);
        if (responseModel.isError()) {
            log.error("文件服务调用失败：{}", responseModel.getMessage());
            return null;
        }
        downloadFileModel.setFileByte(responseModel.getBody());
        String fileType = FileUtil.extName(responseModel.getMessage());
        downloadFileModel.setDownloadFileName(docTitle + "." + fileType);
        return downloadFileModel;
    }


    private boolean checkTimeOut(Date startDate) {
        if (15 < DateUtil.betweenDay(startDate, new Date(), true)) {
            return true;
        }
        return false;
    }

    @Override
    public String getDownloadUrl(Long helpRecordId) {
        return globalConfig.getCloudDomain() + "/doc-delivery/file/download/" + helpRecordId;
    }

}
