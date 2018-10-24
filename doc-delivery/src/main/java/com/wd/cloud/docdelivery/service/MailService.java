package com.wd.cloud.docdelivery.service;

import com.wd.cloud.docdelivery.enums.ChannelEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;

/**
 * @author He Zhigang
 * @date 2018/5/17
 * @Description:
 */
public interface MailService {

    /**
     * 发送邮件
     *
     * @param channelEnum    渠道enum
     * @param helperScname   机构名称
     * @param helperEmail    求助者邮箱
     * @param docTitle       文献标题
     * @param downloadUrl    下载链接
     * @param helpStatusEnum 求助状态
     */
    void sendMail(ChannelEnum channelEnum, String helperScname, String helperEmail, String docTitle, String downloadUrl, HelpStatusEnum helpStatusEnum);

    /**
     * 发送邮件
     *
     * @param channel        渠道code
     * @param helperScname   机构名称
     * @param helperEmail    求助者邮箱
     * @param docTitle       文献标题
     * @param downloadUrl    下载链接
     * @param helpStatusEnum 求助状态
     */
    void sendMail(Integer channel, String helperScname, String helperEmail, String docTitle, String downloadUrl, HelpStatusEnum helpStatusEnum);

    /**
     * 发送邮件
     *
     * @param channel      渠道code
     * @param helperScname 机构名称
     * @param helperEmail  求助者邮箱
     * @param docTitle     文献标题
     * @param downloadUrl  下载链接
     * @param processType  处理方式
     */
    void sendMail(Integer channel, String helperScname, String helperEmail, String docTitle, String downloadUrl, Integer processType);

    /**
     * 通知邮件
     *
     * @param channel      渠道code
     * @param helperScname 机构名称
     * @param helperEmail  求助者邮箱
     */
    void sendNotifyMail(Integer channel, String helperScname, String helperEmail);
}
