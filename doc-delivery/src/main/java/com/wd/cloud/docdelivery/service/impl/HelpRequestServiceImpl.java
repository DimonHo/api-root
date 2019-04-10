package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.enums.GiveStatusEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.RepeatHelpRequestException;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
import com.wd.cloud.docdelivery.pojo.entity.DocFile;
import com.wd.cloud.docdelivery.pojo.entity.GiveRecord;
import com.wd.cloud.docdelivery.pojo.entity.HelpRecord;
import com.wd.cloud.docdelivery.pojo.entity.Literature;
import com.wd.cloud.docdelivery.repository.DocFileRepository;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.service.HelpRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/28 14:40
 * @Description:
 */
@Slf4j
@Service("helpRequest")
public class HelpRequestServiceImpl implements HelpRequestService {

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    PdfSearchServerApi pdfSearchServerApi;


    @Override
    public void helpRequest(Literature literature, HelpRecord helpRecord) {
        literature.createUnid();
        Optional<Literature> optionalLiterature = literatureRepository.findByUnid(literature.getUnid());
        if (optionalLiterature.isPresent()) {
            Literature lt = optionalLiterature.get();
            // 最近15天是否求助过相同的文献
            helpRecordRepository
                    .findByHelperEmailAndLiteratureId(helpRecord.getHelperEmail(), lt.getId())
                    .ifPresent(h -> {
                        throw new RepeatHelpRequestException();
                    });
            BeanUtil.copyProperties(literature, lt, CopyOptions.create().setIgnoreNullValue(true));
            literature = lt;
        }
        Literature literatureEntity = literatureRepository.save(literature);
        helpRecord.setLiteratureId(literatureEntity.getId());
        DocFile reusingDocFile = docFileRepository.findByLiteratureIdAndReusingIsTrue(literatureEntity.getId());
        // 如果有复用文件，自动应助成功
        if (null != reusingDocFile) {
            autoGive(reusingDocFile, helpRecord);
        } else {
            bigDbGive(literatureEntity, helpRecord);
        }
        if (helpRecord.getId() == null) {
            helpRecordRepository.save(helpRecord);
        }
    }


    /**
     * 自动应助
     *
     * @param reusingDocFile
     * @param helpRecord
     */
    public HelpRecord autoGive(DocFile reusingDocFile, HelpRecord helpRecord) {
        //先保存求助记录，得到求助ID，再关联应助记录
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
        helpRecord = helpRecordRepository.save(helpRecord);
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setFileId(reusingDocFile.getFileId())
                .setType(GiveTypeEnum.AUTO.value())
                .setGiverName(GiveTypeEnum.AUTO.name())
                .setStatus(GiveStatusEnum.SUCCESS.value())
                .setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
        return helpRecord;
    }

    /**
     * 数据平台应助
     *
     * @param literature
     * @param helpRecord
     */
    @Async
    public HelpRecord bigDbGive(Literature literature, HelpRecord helpRecord) {
        try {
            ResponseModel<String> pdfResponse = pdfSearchServerApi.search(literature);
            if (!pdfResponse.isError()) {
                String fileId = pdfResponse.getBody();
                //先保存求助记录，得到求助ID，再关联应助记录
                helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
                helpRecord = helpRecordRepository.save(helpRecord);

                DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(fileId, literature.getId()).orElse(new DocFile());
                docFile.setFileId(fileId).setLiteratureId(literature.getId()).setBigDb(true);
                docFileRepository.save(docFile);

                GiveRecord giveRecord = new GiveRecord();
                giveRecord.setFileId(fileId)
                        .setType(GiveTypeEnum.BIG_DB.value())
                        .setGiverName(GiveTypeEnum.BIG_DB.name())
                        .setStatus(GiveStatusEnum.SUCCESS.value());
                giveRecord.setHelpRecordId(helpRecord.getId());
                giveRecordRepository.save(giveRecord);
            }
        } catch (Exception e) {
            log.warn("pdfsearch-server调用失败");
        }
        return helpRecord;
    }
}
