package com.wd.cloud.uoserver.constants;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author He Zhigang
 * @date 2019/1/19
 * @Description:
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(value = "global")
public class GlobalProperties {

    private String gatewayUrl;

    private String imgUploadPath;
}
