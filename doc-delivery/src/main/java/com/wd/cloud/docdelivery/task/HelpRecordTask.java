package com.wd.cloud.docdelivery.task;

import com.wd.cloud.docdelivery.entity.VHelpRecord;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.LiteratureRepository;
import com.wd.cloud.docdelivery.repository.VHelpRecordRepository;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wu QiLong
 * @date 2018/12/17
 * @Description: 定时发送未发送的邮件
 */
@Component
public class HelpRecordTask {
    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    VHelpRecordRepository vHelpRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;
    @Autowired
    FileService fileService;
    @Autowired
    MailService mailService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateGiveRecord() {
        //查询所有状态为未发送的求助记录
        List<VHelpRecord> bySend = vHelpRecordRepository.findBySend(false);
        bySend.forEach(vHelpRecord -> mailService.sendMail(vHelpRecord));
    }
}
