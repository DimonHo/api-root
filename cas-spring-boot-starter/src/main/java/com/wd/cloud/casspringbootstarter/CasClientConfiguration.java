package com.wd.cloud.casspringbootstarter;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/22 15:45
 * @Description:
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CasClientConfigurationProperties.class)
public class CasClientConfiguration {
    @Autowired
    CasClientConfigurationProperties configProps;

    @Autowired
    CasClientConfigurationProperties casProperties;

    /**
     * 登出监听器
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegist() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> srb = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>();
        srb.setListener(new SingleSignOutHttpSessionListener());
        log.info("SingleSignOutHttpSessionListener监听器注册成功！");
        return srb;
    }

    /**
     * 登出过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<SingleSignOutFilter> singleSignOutFilterRegistration() {
        FilterRegistrationBean<SingleSignOutFilter> registrationBean = new FilterRegistrationBean<>(new SingleSignOutFilter());
        registrationBean.addUrlPatterns(casProperties.getLogoutUrlPatterns());
        registrationBean.addInitParameter("casServerUrlPrefix", casProperties.getServerUrlPrefix());
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(1);
        log.info("CAS Single Sign Out Filter过滤器注册成功！");
        return registrationBean;

    }

    /**
     * ticket过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<TicketFilter> ticketFilterRegistration() {
        FilterRegistrationBean<TicketFilter> registrationBean = new FilterRegistrationBean<>(new TicketFilter());
        registrationBean.addInitParameter("casServerLoginUrl", casProperties.getServerLoginUrl());
        registrationBean.addInitParameter("serverName", casProperties.getClientHostUrl());
        registrationBean.addUrlPatterns(casProperties.getAuthenticationUrlPatterns());
        registrationBean.setName("CAS check login Filter");
        registrationBean.setOrder(2);
        log.info("CAS check login Filter过滤器注册成功！");
        return registrationBean;
    }

//    /**
//     * 认证过滤器
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
//        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>(new AuthenticationFilter());
//        registrationBean.addInitParameter("casServerLoginUrl", casProperties.getServerLoginUrl());
//        registrationBean.addInitParameter("serverName", casProperties.getClientHostUrl());
//        registrationBean.addUrlPatterns(casProperties.getAuthenticationUrlPatterns());
//        registrationBean.setName("CAS Authentication Filter");
//        registrationBean.setOrder(3);
//        log.info("CAS Authentication Filter过滤器注册成功！");
//        return registrationBean;
//    }

    /**
     * ticket校验过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> cas30ProxyReceivingTicketValidationFilterRegistration() {
        FilterRegistrationBean<Cas30ProxyReceivingTicketValidationFilter> registrationBean = new FilterRegistrationBean<>(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addInitParameter("casServerUrlPrefix", casProperties.getServerUrlPrefix());
        registrationBean.addInitParameter("serverName", casProperties.getClientHostUrl());
        // redirectAfterValidation验证完成后不重定向，防止ajax调用api的时候出现重定向跨域问题
        registrationBean.addInitParameter("redirectAfterValidation", "false");
        registrationBean.addUrlPatterns(casProperties.getValidationUrlPatterns());
        registrationBean.setName("CAS Validation Filter");
        registrationBean.setOrder(4);
        log.info("CAS Validation Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpServletRequestWrapperFilter> httpServletRequestWrapperFilterRegistration() {
        FilterRegistrationBean<HttpServletRequestWrapperFilter> registrationBean = new FilterRegistrationBean<>(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns(casProperties.getRequestWrapperUrlPatterns());
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(5);
        log.info("CAS HttpServletRequest Wrapper Filter过滤器注册成功！");
        return registrationBean;
    }

}
