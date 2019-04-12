package com.lzh.mvcframework.annotation;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LzhAutowired {

    String value() default "";
}
