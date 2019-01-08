package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.entity.HelpRecord;
import com.wd.cloud.docdelivery.entity.VHelpRecord;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.model.MailModel;
import com.wd.cloud.docdelivery.model.MailTemplateModel;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.service.MailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

    @Override
    public void sendMail(VHelpRecord vHelpRecord) {
        List<String> tos = splitAddress(vHelpRecord.getHelperEmail());
        if (tos == null) {
            throw new NullPointerException("收件人邮箱为空！");
        }
        MailTemplateModel mailTemplateModel = buildMailTemplateModel(vHelpRecord);
        MailModel mailModel = new MailModel();
        mailModel.setTos(ArrayUtil.toArray(tos, String.class));
        mailModel.setTitle(mailTemplateModel.getMailTitle())
                .setContent(buildContent(mailTemplateModel, vHelpRecord.getStatus(), vHelpRecord.getChannelName()));
        Optional<HelpRecord> optionalHelpRecord = helpRecordRepository.findById(vHelpRecord.getHelperId());
        optionalHelpRecord.ifPresent(helpRecord -> excuteSend(mailModel, helpRecord));
    }

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
                            .setHelperName(vHelpRecord.getHelperName())
                            .setHelperOrgName(vHelpRecord.getHelperScname())
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getHelperScname() + "-notify"));
                    break;
                case HELP_THIRD:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•疑难文献]-%s", vHelpRecord.getDocTitle()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getHelperScname() + "-third"));
                    break;
                case HELP_SUCCESSED:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•成功]-%s", vHelpRecord.getDocTitle()))
                            .setDownloadUrl(buildDownloadUrl(vHelpRecord.getHelpRecordId()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getHelperScname() + "-success"));
                    break;
                case HELP_FAILED:
                    mailTemplateModel.setMailTitle(String.format("[文献互助•失败]-%s", vHelpRecord.getDocTitle()))
                            .setTemplate(String.format(vHelpRecord.getChannelTemplate(), vHelpRecord.getHelperScname() + "-failed"));
                    break;
                default:
                    mailTemplateModel.setMailTitle("错误邮件");
                    break;
            }
        }
        return mailTemplateModel;
    }

    private void excuteSend(MailModel mailModel, HelpRecord helpRecord) {
        try {
            mailModel.send();
            helpRecord.setSend(true);
            helpRecordRepository.save(helpRecord);
        } catch (Exception e) {
            helpRecord.setSend(false);
            helpRecordRepository.save(helpRecord);
            log.error("发送邮件至{}失败：", helpRecord.getHelperEmail(), e);
        }
    }


    /**
     * 构建邮件内容
     *
     * @param helpStatusEnum
     * @param helperScname
     * @return
     */
    private String buildContent(MailTemplateModel mailTemplateModel, Integer helpStatusEnum, String helperScname) {
        String templateFile = mailTemplateModel.getTemplate();
        if (templateNotExists(templateFile)) {
            log.info("模板文件[{}]不存在，使用默认模板", templateFile);
            templateFile = templateFile.replace(StrUtil.subBefore(templateFile, "-", false), "default");
        }
        String content = null;
        boolean isDefault = templateFile.contains("default");
        try {
            // 如果templateFile不包含“default”字符串，就去外部目录去加载
            if (!isDefault) {
                freeMarkerConfigurer.getConfiguration().setDirectoryForTemplateLoading(new File(global.getTemplatesBase()));
            }
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateFile);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailTemplateModel);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 检查模板文件是否存在
     *
     * @param templateFile
     * @return
     */
    private boolean templateNotExists(String templateFile) {
        String suffix = "/";
        String path = global.getTemplatesBase() + suffix + templateFile;
        if (StrUtil.endWith(global.getTemplatesBase(), suffix)) {
            path = global.getTemplatesBase() + templateFile;
        }
        return !FileUtil.exist(path);
    }


    /**
     * 收件人地址拆分
     *
     * @param addresses
     * @return
     */
    private static List<String> splitAddress(String addresses) {
        if (StrUtil.isBlank(addresses)) {
            return null;
        }

        List<String> result;
        if (StrUtil.contains(addresses, ',')) {
            result = StrUtil.splitTrim(addresses, ',');
        } else if (StrUtil.contains(addresses, ';')) {
            result = StrUtil.splitTrim(addresses, ';');
        } else {
            result = CollUtil.newArrayList(addresses);
        }
        return result;
    }

    private String buildDownloadUrl(Long helpRecordId) {
        return global.getCloudDomain() + "/doc-delivery/file/download/" + helpRecordId;
    }
}
