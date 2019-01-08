package com.neo.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {
    String tableAlias();

    String tableColumn();

    String userInfoColumn();

    int argLocation() default 0;
}
