package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.entity.DocFile;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
import com.wd.cloud.docdelivery.model.DownloadFileModel;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
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
    Global global;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    PdfSearchServerApi pdfSearchServerApi;

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
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecord(helpRecord);
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    @Override
    public DownloadFileModel getWaitAuditFile(Long helpRecordId) {
        HelpRecord helpRecord = helpRecordRepository.getOne(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordAndAuditStatusEquals(helpRecord, AuditEnum.WAIT.value());
        DownloadFileModel downloadFileModel = buildDownloadModel(helpRecord, giveRecord);
        return downloadFileModel;
    }

    private DownloadFileModel buildDownloadModel(HelpRecord helpRecord, GiveRecord giveRecord) {
        String fileId = giveRecord.getDocFile().getFileId();
        String fileName = giveRecord.getDocFile().getFileName();
        String docTitle = helpRecord.getLiterature().getDocTitle();
        //以文献标题作为文件名，标题中可能存在不符合系统文件命名规范，在这里规范一下。
        docTitle = FileUtil.cleanInvalid(docTitle);
        DownloadFileModel downloadFileModel = new DownloadFileModel();
        ResponseModel<byte[]> responseModel = giveRecord.getGiverType() == GiveTypeEnum.BIG_DB.value() ?
                pdfSearchServerApi.getFileByte(fileId) : fsServerApi.getFileByte(fileId);
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
        return global.getCloudDomain() + "/doc-delivery/file/download/" + helpRecordId;
    }

    @Override
    public int buildFileId() {
        AtomicInteger count = new AtomicInteger();
        Pageable pageable = PageRequest.of(0, 1000);
        Page<DocFile> docFiles = docFileRepository.findAll(pageable);
        count = updateFileId(docFiles, count);
        while (docFiles.hasNext()) {
            docFiles = docFileRepository.findAll(docFiles.nextPageable());
            count = updateFileId(docFiles, count);
        }
        log.info("共更新{}条数据", count.get());
        return count.get();
    }

    private AtomicInteger updateFileId(Page<DocFile> docFiles, AtomicInteger count) {
        docFiles.forEach(docFile -> {
            String md5 = StrUtil.subBefore(docFile.getFileName(), ".", true);
            String path = "doc-delivery";
            docFile.setFileId(SecureUtil.md5(docFile.getFileName() + path));
            docFileRepository.save(docFile);
            count.getAndIncrement();
        });
        return count;
    }
}
