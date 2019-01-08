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
        emailService.sendHtmlEmail(user.getEmail(), "������ʾ", "<p style='font:20px;font-weight:bold;'>���ĵ�½���ǣ�<span style='color:red'>" + user.getUsername() + "</span>���ߵ�ǰ����,�����ǣ�<span style='color:red'>" + user.getPassword() + "</span>,�״ε�½���޸����룡</p>");
        user.setPassword("");
    }
}
