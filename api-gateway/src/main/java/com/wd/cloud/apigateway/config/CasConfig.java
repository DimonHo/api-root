package com.wd.cloud.apigateway.config;

import com.wd.cloud.apigateway.filter.CorsFilter;
import com.wd.cloud.apigateway.filter.TicketFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AuthenticationFilter;
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
@Configuration
@EnableConfigurationProperties(CasProperties.class)
public class CasConfig implements WebMvcConfigurer {

    @Autowired
    private CasProperties casProperties;

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegist() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> srb = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>();
        srb.setListener(new SingleSignOutHttpSessionListener());
        log.info("SingleSignOutHttpSessionListener监听器注册成功！");
        return srb;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>(new CorsFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("Response Cors Filter");
        registrationBean.setOrder(0);
        log.info("Response Cors Filter过滤器注册成功！");
        return registrationBean;

    }

    @Bean
    public FilterRegistrationBean<SingleSignOutFilter> singleSignOutFilterRegistration() {
        FilterRegistrationBean<SingleSignOutFilter> registrationBean = new FilterRegistrationBean<>(new SingleSignOutFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("casServerUrlPrefix", casProperties.getServerUrlPrefix());
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(1);
        log.info("CAS Single Sign Out Filter过滤器注册成功！");
        return registrationBean;

    }

    @Bean
    public FilterRegistrationBean<TicketFilter> ticketFilterRegistration() {
        FilterRegistrationBean<TicketFilter> registrationBean = new FilterRegistrationBean<>(new TicketFilter());
        registrationBean.addInitParameter("casServerLoginUrl", casProperties.getServerUrlPrefix() + "/login");
        registrationBean.addInitParameter("serverName", casProperties.getClientHostUrl());
        registrationBean.addUrlPatterns("/userinfo");
        registrationBean.setName("CAS check login Filter");
        registrationBean.setOrder(2);
        log.info("CAS check login Filter过滤器注册成功！");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>(new AuthenticationFilter());
        registrationBean.addInitParameter("authenticationRedirectStrategyClass", "com.wd.cloud.apigateway.config.ApiAuthenticationRedirectStrategy");
        registrationBean.addInitParameter("casServerLoginUrl", casProperties.getServerUrlPrefix() + "/login");
        registrationBean.addInitParameter("serverName", casProperties.getClientHostUrl());
        registrationBean.addUrlPatterns("/userinfo");
        registrationBean.setName("CAS Authentication Filter");
        registrationBean.setOrder(3);
        log.info("CAS Authentication Filter过滤器注册成功！");
        return registrationBean;
    }

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
