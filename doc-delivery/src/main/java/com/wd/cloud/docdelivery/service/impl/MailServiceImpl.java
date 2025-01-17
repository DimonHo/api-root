package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.model.MailTemplateModel;
import com.wd.cloud.docdelivery.pojo.entity.HelpRecord;
import com.wd.cloud.docdelivery.pojo.entity.VHelpRecord;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.service.MailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
@Slf4j
@Service("mailService")
public class MailServiceImpl implements MailService {

    @Autowired
    Global global;
    @Autowired
    HelpRecordRepository helpRecordRepository;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Async
    @Override
    public void sendMail(VHelpRecord vHelpRecord) {

        MailTemplateModel mailTemplateModel = buildMailTemplateModel(vHelpRecord);
        String mailTitle = mailTemplateModel.getMailTitle();
        String mailContent = buildContent(mailTemplateModel);
        Optional<HelpRecord> optionalHelpRecord = helpRecordRepository.findById(vHelpRecord.getId());
        if (optionalHelpRecord.isPresent()) {
            HelpRecord helpRecord = optionalHelpRecord.get();
            try {
                if (HelpStatusEnum.WAIT_HELP.value() == helpRecord.getStatus()) {
                    MailUtil.send(global.getNotifyMail(), null, CollectionUtil.newArrayList(vHelpRecord.getBccs()), mailTitle, mailContent, true);
                } else {
                    MailUtil.send(CollectionUtil.newArrayList(helpRecord.getHelperEmail()), null, CollectionUtil.newArrayList(vHelpRecord.getBccs()), mailTitle, mailContent, true);
                }
                helpRecord.setSend(true);
            } catch (MailException e) {
                if (ExceptionUtil.isCausedBy(e, SMTPAddressFailedException.class)) {
                    helpRecord.setSend(true);
                    log.error("邮件地址{}不可达！", helpRecord.getHelperEmail(), e);
                } else {
                    helpRecord.setSend(false);
                    log.error("发送邮件至{}失败！", helpRecord.getHelperEmail(), e);
                }
            } finally {
                helpRecordRepository.save(helpRecord);
            }
        }
        optionalHelpRecord.ifPresent(helpRecord -> {

        });
    }

    /**
     * 构建邮件内容模板
     *
     * @param vHelpRecord
     * @return
     */
    private MailTemplateModel buildMailTemplateModel(VHelpRecord vHelpRecord) {
        MailTemplateModel mailTemplateModel = new MailTemplateModel();
        mailTemplateModel.setChannelName(vHelpRecord.getChannelName())
                .setChannelUrl(vHelpRecord.getChannelUrl())
                .setDocTitle(vHelpRecord.getDocTitle());

        Optional<HelpStatusEnum> optionalHelpStatusEnum = HelpStatusEnum.match(vHelpRecord.getStatus());
        if (optionalHelpStatusEnum.isPresent()) {
            switch (optionalHelpStatusEnum.get()) {
                case WAIT_HELP:
                    mailTemplateModel.setMailTitle("用户文献互助")
                            .setHelperEmail(vHelpRecord.getHelperEmail())
                            .setOrgName(vHelpRecord.getOrgName())
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getOrgName() + "-notify"));
                    break;
                case HELP_THIRD:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•疑难文献]-%s", vHelpRecord.getDocTitle()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getOrgName() + "-third"));
                    break;
                case HELP_SUCCESSED:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•成功]-%s", vHelpRecord.getDocTitle()))
                            .setDownloadUrl(buildDownloadUrl(vHelpRecord.getId()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getOrgName() + "-success"));
                    break;
                case HELP_FAILED:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•失败]-%s", vHelpRecord.getDocTitle()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getOrgName() + "-failed"));
                    break;
                default:
                    throw new MailException("邮件构建错误");
            }
        }
        return mailTemplateModel;
    }


    /**
     * 构建邮件内容
     *
     * @return
     */
    private String buildContent(MailTemplateModel mailTemplateModel) {
        String templateFile = mailTemplateModel.getTemplate();
        String content = null;
        Template template;
        try {
            template = freeMarkerConfigurer.getConfiguration().getTemplate(templateFile);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailTemplateModel);
        } catch (TemplateException | IOException e) {
            log.info("模板文件[{}]不存在，使用默认模板", templateFile);
            templateFile = templateFile.replace(StrUtil.subBefore(templateFile, "-", false), "default");
            try {
                template = freeMarkerConfigurer.getConfiguration().getTemplate(templateFile);
                content = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailTemplateModel);
            } catch (TemplateException | IOException e1) {
                log.error("模板文件[" + templateFile + "]不存在!", e1);
            }
        }
        return content;
    }


    /**
     * 构建全文下载链接
     *
     * @param helpRecordId
     * @return
     */
    private String buildDownloadUrl(Long helpRecordId) {
        return global.getCloudDomain() + "/doc-delivery/file/download/" + helpRecordId;
    }
}
