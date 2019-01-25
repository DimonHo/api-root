package com.wd.cloud.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author He Zhigang
 * @date 2019/1/25
 * @Description:
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String localUrl = "http://172.16.10.7:8080";

    private static final String ssoUrl = "http://sso.test.hnlat.com";

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
        registrationBean.addInitParameter("casServerUrlPrefix", ssoUrl);
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(1);
        log.info("CAS Single Sign Out Filter过滤器注册成功！");
        return registrationBean;

    }


    @Bean
    public FilterRegistrationBean<HttpServletRequestWrapperFilter> httpServletRequestWrapperFilterRegistration() {
        FilterRegistrationBean<HttpServletRequestWrapperFilter> registrationBean = new FilterRegistrationBean<>(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(2);
        log.info("CAS HttpServletRequest Wrapper Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>(new AuthenticationFilter());
        registrationBean.addInitParameter("casServerLoginUrl", ssoUrl + "/login");
        registrationBean.addInitParameter("serverName", localUrl);
        registrationBean.addUrlPatterns("/login");
        registrationBean.setName("CAS Authentication Filter");
        registrationBean.setOrder(3);
        log.info("CAS Authentication Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> cas30ProxyReceivingTicketValidationFilterRegistration() {
        FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> registrationBean = new FilterRegistrationBean<>(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addInitParameter("casServerUrlPrefix", ssoUrl);
        registrationBean.addInitParameter("serverName", localUrl);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS Validation Filter");
        registrationBean.setOrder(4);
        log.info("CAS Validation Filter过滤器注册成功！");
        return registrationBean;
    }

}
