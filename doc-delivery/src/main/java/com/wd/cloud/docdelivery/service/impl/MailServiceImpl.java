package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.docdelivery.config.GlobalConfig;
import com.wd.cloud.docdelivery.enums.ChannelEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.model.*;
import com.wd.cloud.docdelivery.service.MailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
@Async
@Service("mailService")
public class MailServiceImpl implements MailService {

    private static final Log log = LogFactory.get();

    @Autowired
    MailModel spis;

    @Autowired
    MailModel crs;

    @Autowired
    MailModel zhy;

    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    private FreeMarkerConfigurer configuration;

    /**
     * 地址拆分
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

    @Override
    public void sendMail(Integer channel, String helperScname, String helpEmail, String docTitle, String downloadUrl, Integer processType) {
        HelpStatusEnum helpStatusEnum = null;
        for (HelpStatusEnum helpStatusInstance : HelpStatusEnum.values()) {
            if (helpStatusInstance.getCode() == processType) {
                helpStatusEnum = helpStatusInstance;
            }
        }
        sendMail(channel, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum);
    }

    @Override
    public void sendMail(Integer channel, String helperScname, String helpEmail, String docTitle, String downloadUrl, HelpStatusEnum helpStatusEnum) {
        ChannelEnum channelEnum = getChannelEnum(channel);
        sendMail(channelEnum, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum);
    }

    @Override
    public void sendMail(ChannelEnum channelEnum, String helperScname, String helpEmail, String docTitle, String downloadUrl, HelpStatusEnum helpStatusEnum) {
        if (ChannelEnum.SPIS.equals(channelEnum)) {
            sendMail(spis, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum);
        } else if (ChannelEnum.CRS.equals(channelEnum)) {
            sendMail(crs, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum);
        } else if (ChannelEnum.ZHY.equals(channelEnum)) {
            sendMail(zhy, helperScname, helpEmail, docTitle, downloadUrl, helpStatusEnum);
        }
    }

    @Override
    public void sendNotifyMail(Integer channel, String helperScname, String helpEmail) {
        ChannelEnum channelEnum = getChannelEnum(channel);
        if (ChannelEnum.SPIS.equals(channelEnum)) {
            sendNotifyMail(spis, helperScname, helpEmail);
        }
        if (ChannelEnum.CRS.equals(channelEnum)) {
            sendNotifyMail(crs, helperScname, helpEmail);
        }
        if (ChannelEnum.ZHY.equals(channelEnum)) {
            sendNotifyMail(zhy, helperScname, helpEmail);
        }
    }

    private void sendMail(MailModel mailModel, String helperScname, String helpEmail, String docTitle, String downloadUrl, HelpStatusEnum helpStatusEnum) {
        List<String> tos = splitAddress(helpEmail);
        mailModel.setTos(tos.toArray(new String[tos.size()]));
        if (HelpStatusEnum.HELP_SUCCESSED.equals(helpStatusEnum)) {
            DefaultMailSuccessModel successModel = new DefaultMailSuccessModel();
            successModel.setDownloadUrl(downloadUrl).setDocTitle(docTitle);
            mailModel.setSuccessModel(successModel);
            mailModel.setTitle(String.format(mailModel.getSuccessModel().getMailTitle(), docTitle))
                    .setContent(buildContent(mailModel, helpStatusEnum, helperScname));
        } else if (HelpStatusEnum.HELP_THIRD.equals(helpStatusEnum)) {
            DefaultMailThirdModel thirdModel = new DefaultMailThirdModel();
            thirdModel.setDocTitle(docTitle);
            mailModel.setThirdModel(thirdModel);
            mailModel.setTitle(String.format(mailModel.getThirdModel().getMailTitle(), docTitle))
                    .setContent(buildContent(mailModel, helpStatusEnum, helperScname));

        } else if (HelpStatusEnum.HELP_FAILED.equals(helpStatusEnum)) {
            DefaultMailFailedModel failedModel = new DefaultMailFailedModel();
            failedModel.setDocTitle(docTitle);
            mailModel.setFailedModel(failedModel);
            mailModel.setTitle(String.format(mailModel.getFailedModel().getMailTitle(), docTitle))
                    .setContent(buildContent(mailModel, helpStatusEnum, helperScname));
        }
        mailModel.send();
    }

    private void sendNotifyMail(MailModel mailModel, String helperScname, String helpEmail) {
        DefaultMailNotifyModel notifyModel = new DefaultMailNotifyModel();
        notifyModel.setHelperScname(helperScname).setHelperName(helpEmail);
        mailModel.setTos(globalConfig.getNotifyMail());
        mailModel.setNotifyModel(notifyModel);
        mailModel.setTitle(mailModel.getNotifyModel().getMailTitle())
                .setContent(buildNotifyContent(mailModel, helperScname));
        mailModel.send();
    }

    /**
     * 获取渠道
     *
     * @param channel
     * @return
     */
    private ChannelEnum getChannelEnum(Integer channel) {
        ChannelEnum channelEnum = null;
        for (ChannelEnum channelInstance : ChannelEnum.values()) {
            if (channelInstance.getCode() == channel) {
                channelEnum = channelInstance;
            }
        }
        return channelEnum;
    }

    /**
     * 构建邮件内容
     *
     * @param mailModel
     * @param helpStatusEnum
     * @param helperScname
     * @return
     */
    private String buildContent(MailModel mailModel, HelpStatusEnum helpStatusEnum, String helperScname) {
        String templateFile = null;
        if (HelpStatusEnum.HELP_SUCCESSED.equals(helpStatusEnum)) {
            templateFile = String.format(mailModel.getTemplateFile(), helperScname + "-success");
            if (!FileUtil.exist(templateFile)) {
                log.info("模板文件[{}]不存在，使用默认模板", templateFile);
                templateFile = "default-success.ftl";
            } else {
                templateFile = StrUtil.subAfter(templateFile, "templates/", true);
            }
        } else if (HelpStatusEnum.HELP_FAILED.equals(helpStatusEnum)) {
            templateFile = String.format(mailModel.getTemplateFile(), helperScname + "-failed");
            if (!FileUtil.exist(templateFile)) {
                templateFile = "default-failed.ftl";
            } else {
                templateFile = StrUtil.subAfter(templateFile, "templates/", true);
            }
        } else if (HelpStatusEnum.HELP_THIRD.equals(helpStatusEnum)) {
            templateFile = String.format(mailModel.getTemplateFile(), helperScname + "-third");
            if (!FileUtil.exist(templateFile)) {
                templateFile = "default-third.ftl";
            } else {

            }
        }
        return buildContent(mailModel, templateFile);
    }

    private String buildNotifyContent(MailModel mailModel, String helperScname) {
        String templateFile = String.format(mailModel.getTemplateFile(), helperScname + "-notify");
        if (!FileUtil.exist(templateFile)) {
            templateFile = "default-notify.ftl";
        }
        return buildContent(mailModel, templateFile);
    }

    private String buildContent(MailModel mailModel, String templateFile) {
        String content = null;
        try {
            templateFile = templateFile.contains("templates/") ? StrUtil.subAfter(templateFile, "templates/", true) : templateFile;
            Template template = configuration.getConfiguration().getTemplate(templateFile);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailModel);
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
