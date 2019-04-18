package com.lzh.spring.formework.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LzhAutowired {

    String value() default "";
}
