package com.wd.cloud.commons.annotation;

import java.lang.annotation.*;

/**
 * @author He Zhigang
 * @date 2019/1/28
 * @Description: 验证请求中的用户是否合法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateUser {
}
