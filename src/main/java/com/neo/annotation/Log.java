package com.neo.annotation;

import com.neo.enums.OperateType;
import com.neo.enums.OperatorType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author ruoyi
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    OperateType businessType() default OperateType.OTHER;

    /**
     * 操作人类别
     */
    OperatorType operatorType() default OperatorType.MANAGE;
    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;
}
