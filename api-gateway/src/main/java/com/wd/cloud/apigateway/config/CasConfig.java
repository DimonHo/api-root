package com.wd.cloud.apigateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author He Zhigang
 * @date 2019/1/25
 * @Description:
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(value = "cas")
public class CasConfig implements WebMvcConfigurer {

    private String clientHostUrl;

    private String serverUrlPrefix;

    private String serverLoginUrl;

    private String authenticationUrlPatterns;

    private String validationUrlPatterns;

    private String requestWrapperUrlPatterns;

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegist() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> srb = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>();
        srb.setListener(new SingleSignOutHttpSessionListener());
        log.info("SingleSignOutHttpSessionListener监听器注册成功！");
        return srb;
    }

    @Bean
    public FilterRegistrationBean<SingleSignOutFilter> singleSignOutFilterRegistration() {
        FilterRegistrationBean<SingleSignOutFilter> registrationBean = new FilterRegistrationBean<>(new SingleSignOutFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("casServerUrlPrefix", serverUrlPrefix);
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(1);
        log.info("CAS Single Sign Out Filter过滤器注册成功！");
        return registrationBean;

    }


    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>(new AuthenticationFilter());
        registrationBean.addInitParameter("casServerLoginUrl", serverUrlPrefix + "/login");
        registrationBean.addInitParameter("serverName", clientHostUrl);
        registrationBean.addUrlPatterns(authenticationUrlPatterns.split(","));
        registrationBean.setName("CAS Authentication Filter");
        registrationBean.setOrder(2);
        log.info("CAS Authentication Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> cas30ProxyReceivingTicketValidationFilterRegistration() {
        FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> registrationBean = new FilterRegistrationBean<>(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addInitParameter("casServerUrlPrefix", serverUrlPrefix);
        registrationBean.addInitParameter("serverName", clientHostUrl);
        registrationBean.addUrlPatterns(validationUrlPatterns);
        registrationBean.setName("CAS Validation Filter");
        registrationBean.setOrder(3);
        log.info("CAS Validation Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpServletRequestWrapperFilter> httpServletRequestWrapperFilterRegistration() {
        FilterRegistrationBean<HttpServletRequestWrapperFilter> registrationBean = new FilterRegistrationBean<>(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns(requestWrapperUrlPatterns);
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(4);
        log.info("CAS HttpServletRequest Wrapper Filter过滤器注册成功！");
        return registrationBean;
    }

}
