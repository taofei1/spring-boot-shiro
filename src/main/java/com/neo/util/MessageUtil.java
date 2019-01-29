package com.neo.util;

import com.neo.quartz.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * 根据消息键和参数 获取消息 委托给spring messageSource
 */
@Slf4j
public class MessageUtil {


    public static String message(String code, Object... args) {
        MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
        return messageSource.getMessage(code, args, null);
    }

    public static void main(String[] args) {
        ResourceBundleMessageSource m = new ResourceBundleMessageSource();
        m.setBasename("i18n/messages");
        m.setDefaultEncoding("utf-8");
        System.out.println(m.getMessage("user.login.success", null, null));
    }


}
