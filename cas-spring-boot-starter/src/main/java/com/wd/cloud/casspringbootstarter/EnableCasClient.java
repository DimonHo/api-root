package com.wd.cloud.casspringbootstarter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/22 15:50
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CasClientConfiguration.class)
public @interface EnableCasClient {
}
