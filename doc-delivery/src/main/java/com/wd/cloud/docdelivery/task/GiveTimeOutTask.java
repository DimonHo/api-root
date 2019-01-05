package com.wd.cloud.docdelivery.task;

import cn.hutool.cron.task.Task;
import com.wd.cloud.docdelivery.entity.GiveRecord;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.repository.GiveRecordRepository;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/1/4
 * @Description:
 */
public class GiveTimeOutTask implements Task {
    @Autowired
    GiveRecordRepository giveRecordRepository;
    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Override
    public void execute() {
        List<GiveRecord> giveRecords = giveRecordRepository.findTimeOutRecord();
        giveRecords.forEach(this::updateHelpStatus);
    }

    private void updateHelpStatus(GiveRecord giveRecord) {
        giveRecordRepository.delete(giveRecord);
        HelpRecord helpRecord = helpRecordRepository.findById(giveRecord.getHelpRecordId()).orElse(null);
        if (helpRecord != null) {
            helpRecord.setStatus(0);
            helpRecordRepository.save(helpRecord);
        }
    }
}
