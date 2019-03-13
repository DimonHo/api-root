package com.wd.cloud.docdelivery.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/5/3
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(value = "help.global")
public class Global {

    private String cloudDomain;

    /**
     * 文件在hbase的位置
     */
    private String hbaseTableName;

    /**
     * 上传文件类型
     */
    private List<String> fileTypes;

    private List<String> notifyMail;

}
