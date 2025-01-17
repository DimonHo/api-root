package com.wd.cloud.casspringbootstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/22 15:43
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "cas", ignoreUnknownFields = false)
public class CasClientConfigurationProperties {

    /**
     * CAS server URL E.g. https://example.com/cas or https://cas.example. Required.
     * CAS 服务端 url 不能为空
     */
    private String serverUrlPrefix = "http://sso.hnlat.cc";

    /**
     * CAS server login URL E.g. https://example.com/cas/login or https://cas.example/login. Required.
     * CAS 服务端登录地址  上面的连接 加上/login 该参数不能为空
     */
    private String serverLoginUrl = "http://sso.hnlat.cc/login";

    /**
     * CAS-protected client application host URL E.g. https://myclient.example.com Required.
     * 当前客户端的地址
     */
    private String clientHostUrl = "http://cloud.hnlat.cc";

    /**
     * 忽略规则,访问那些地址 不需要登录
     */
    private String ignorePattern;

    /**
     * 自定义UrlPatternMatcherStrategy验证
     */
    private String ignoreUrlPatternType;

    private String logoutUrlPatterns = "/*";

    private String[] authenticationUrlPatterns;

    private String validationUrlPatterns;

    private String requestWrapperUrlPatterns;

}
