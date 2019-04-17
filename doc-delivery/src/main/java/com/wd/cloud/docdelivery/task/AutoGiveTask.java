package com.wd.cloud.docdelivery.task;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.enums.GiveStatusEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
import com.wd.cloud.docdelivery.pojo.entity.DocFile;
import com.wd.cloud.docdelivery.pojo.entity.GiveRecord;
import com.wd.cloud.docdelivery.pojo.entity.HelpRecord;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: He Zhigang
 * @Date: 2019/4/17 15:04
 * @Description:
 */
@Slf4j
public class AutoGiveTask implements Runnable {

    private GiveRecordRepository giveRecordRepository;
    private HelpRecordRepository helpRecordRepository;
    private LiteratureRepository literatureRepository;
    private DocFileRepository docFileRepository;
    private PdfSearchServerApi pdfSearchServerApi;
    private Long helpRecordId;

    public AutoGiveTask(HelpRecordRepository helpRecordRepository,
                        LiteratureRepository literatureRepository,
                        GiveRecordRepository giveRecordRepository,
                        DocFileRepository docFileRepository,
                        PdfSearchServerApi pdfSearchServerApi,
                        Long helpRecordId) {
        this.giveRecordRepository = giveRecordRepository;
        this.helpRecordRepository = helpRecordRepository;
        this.literatureRepository = literatureRepository;
        this.docFileRepository = docFileRepository;
        this.pdfSearchServerApi = pdfSearchServerApi;
        this.helpRecordId = helpRecordId;
    }

    /**
     * 数据平台应助
     *
     * @param helpRecord
     */
    public boolean bigDbGive(HelpRecord helpRecord) {
        AtomicBoolean success = new AtomicBoolean(false);
        try {
            literatureRepository.findById(helpRecord.getLiteratureId()).ifPresent(literature -> {
                ResponseModel<String> pdfResponse = pdfSearchServerApi.search(literature);
                if (!pdfResponse.isError()) {
                    String fileId = pdfResponse.getBody();

                    DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(fileId, literature.getId()).orElse(new DocFile());
                    docFile.setFileId(fileId).setLiteratureId(literature.getId()).setBigDb(true);

                    GiveRecord giveRecord = new GiveRecord();
                    giveRecord.setFileId(fileId)
                            .setType(GiveTypeEnum.BIG_DB.value())
                            .setGiverName(GiveTypeEnum.BIG_DB.name())
                            .setStatus(GiveStatusEnum.SUCCESS.value());
                    giveRecord.setHelpRecordId(helpRecord.getId());

                    helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
                    docFileRepository.save(docFile);
                    giveRecordRepository.save(giveRecord);
                    helpRecordRepository.save(helpRecord);
                    success.set(true);
                }
            });
        } catch (Exception e) {
            log.warn("pdfsearch-server调用失败");
        }
        return success.get();
    }

    /**
     * 自动应助
     *
     * @param helpRecord
     */
    public boolean reusingGive(DocFile reusingDocFile, HelpRecord helpRecord) {
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setFileId(reusingDocFile.getFileId())
                .setType(GiveTypeEnum.AUTO.value())
                .setGiverName(GiveTypeEnum.AUTO.name())
                .setStatus(GiveStatusEnum.SUCCESS.value())
                .setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
        helpRecordRepository.save(helpRecord);
        return true;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        helpRecordRepository.findById(helpRecordId).ifPresent(helpRecord -> {
            DocFile reusingDocFile = docFileRepository.findByLiteratureIdAndReusingIsTrue(helpRecord.getLiteratureId());
            if (null != reusingDocFile) {
                reusingGive(reusingDocFile, helpRecord);
            } else {
                bigDbGive(helpRecord);
            }
        });
    }
}
