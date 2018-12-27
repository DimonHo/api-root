package com.wd.cloud.docdelivery.filter;


import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RegistFilter {
    @Bean
    public FilterRegistrationBean singleSignOutFilterRegistration(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new SingleSignOutFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("casServerUrlPrefix", "http://sso.test.hnlat.com");
        registrationBean.setName("CAS Single Sign Out Filter");
        registrationBean.setOrder(1);
        return registrationBean;

    }

    @Bean
    public FilterRegistrationBean  httpServletRequestWrapperFilterRegistration(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new HttpServletRequestWrapperFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS HttpServletRequest Wrapper Filter");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean  authenticationFilterRegistration(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new AuthenticationFilter());
        registrationBean.addInitParameter("casServerLoginUrl", "http://sso.test.hnlat.com/login");
        registrationBean.addInitParameter("serverName", "http://192.168.1.132:10012");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS Authentication Filter");
        registrationBean.setOrder(3);
        return registrationBean;
    }
    @Bean
    public FilterRegistrationBean  cas30ProxyReceivingTicketValidationFilterRegistration(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new Cas30ProxyReceivingTicketValidationFilter());
        registrationBean.addInitParameter("casServerUrlPrefix", "http://sso.test.hnlat.com");
        registrationBean.addInitParameter("serverName", "http://192.168.1.132:10012");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CAS Validation Filter");
        registrationBean.setOrder(4);
        return registrationBean;
    }


}
