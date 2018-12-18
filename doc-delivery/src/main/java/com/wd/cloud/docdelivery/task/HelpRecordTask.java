package com.wd.cloud.docdelivery.task;

import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.enums.ChannelEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
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
    FileService fileService;
    @Autowired
    MailService mailService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateGiveRecord() {
        //查询所有状态为未发送的求助记录
        List<HelpRecord> bySend = helpRecordRepository.findBySend(false);
        //循环遍历出所有的未发送邮箱的求助记录
        for (HelpRecord helpRecord : bySend) {
            Integer channel = helpRecord.getHelpChannel();
            ChannelEnum channelEnum = getChannelEnum(channel);
            String helperScname = helpRecord.getHelperScname();
            String helpEmail=  helpRecord.getHelperEmail();
            String docTitle = helpRecord.getLiterature().getDocTitle();
            long id = helpRecord.getId();
            int status = helpRecord.getStatus();
            if (HelpStatusEnum.HELP_THIRD.getCode() == status){
                String  downloadUrl = null;
                HelpStatusEnum helpStatusEnum = HelpStatusEnum.HELP_THIRD;
                mailService.sendMail(channelEnum, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum,id);
            }else if(HelpStatusEnum.HELP_SUCCESSED.getCode() == status){
                String downloadUrl = fileService.getDownloadUrl(helpRecord.getId());
                HelpStatusEnum helpStatusEnum = HelpStatusEnum.HELP_SUCCESSED;
                mailService.sendMail(channelEnum, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum,id);
            }else if(HelpStatusEnum.HELP_FAILED.getCode() == status){
                String  downloadUrl = null;
                HelpStatusEnum helpStatusEnum = HelpStatusEnum.HELP_FAILED;
                mailService.sendMail(channelEnum, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum,id);

            }
        }
    }

    public ChannelEnum getChannelEnum(Integer channel) {
        ChannelEnum channelEnum = null;
        for (ChannelEnum channelInstance : ChannelEnum.values()) {
            if (channelInstance.getCode() == channel) {
                channelEnum = channelInstance;
            }
        }
        return channelEnum;
    }
}
