package com.mszlu.blog.common.aop;

import java.lang.annotation.*;

/**
 * 日志注解
 * Type 代表可以放在方法上，
 * Method 方法上
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operator() default "";
}

