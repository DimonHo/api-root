package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.apifeign.FsServerApi;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.config.GlobalConfig;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.model.DownloadFileModel;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

/**
 * @author He Zhigang
 * @date 2018/5/16
 * @Description:
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Override
    public DownloadFileModel getDownloadFile(Long helpRecordId) {
        HelpRecord helpRecord = helpRecordRepository.getOne(helpRecordId);
        if (checkTimeOut(helpRecord.getGmtModified())) {
            return null;
        }
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecord(helpRecord);
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    @Override
    public DownloadFileModel getWaitAuditFile(Long helpRecordId) {
        HelpRecord helpRecord = helpRecordRepository.getOne(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordAndAuditStatusEquals(helpRecord, AuditEnum.WAIT.getCode());
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    private DownloadFileModel buildDownloadModel(HelpRecord helpRecord, GiveRecord giveRecord) {
        String fileId = giveRecord.getDocFile().getFileId();
        String docTitle = helpRecord.getLiterature().getDocTitle();
        //以文献标题作为文件名，标题中可能存在不符合系统文件命名规范，在这里规范一下。
        docTitle = FileUtil.cleanInvalid(docTitle);
        DownloadFileModel downloadFileModel = new DownloadFileModel();
        ResponseModel<File> responseModel = fsServerApi.getFile(fileId);
        if (!responseModel.isError()) {
            downloadFileModel.setFile(responseModel.getBody());
            downloadFileModel.setFileByte(FileUtil.readBytes(responseModel.getBody()));
        }
        String ext = StrUtil.subAfter(responseModel.getBody().getName(), ".", true);
        String downLoadFileName = docTitle + "." + ext;
        downloadFileModel.setDownloadFileName(downLoadFileName);
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
