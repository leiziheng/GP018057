package com.lzh.spring.formework.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LzhRequestParam {

    String value() default "";
}
