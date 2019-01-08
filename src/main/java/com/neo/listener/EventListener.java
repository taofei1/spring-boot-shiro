package com.neo.listener;

import com.neo.entity.UserInfo;
import com.neo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    @Autowired
    private EmailService emailService;

    @org.springframework.context.event.EventListener
    @Async
    public void sendEmail(UserInfo user) {
        emailService.sendHtmlEmail(user.getEmail(), "密码提示", "<p style='font:20px;font-weight:bold;'>您的登陆名是：<span style='color:red'>" + user.getUsername() + "</span>或者当前邮箱,密码是：<span style='color:red'>" + user.getPassword() + "</span>,首次登陆请修改密码！</p>");
        user.setPassword("");
    }
}
