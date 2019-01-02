package com.neo.web;

import com.neo.annotation.Log;
import com.neo.enums.OperateType;
import com.neo.service.EmailService;
import com.neo.util.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;
    @RequestMapping("/emailTemplate")
    private String emailTemplate(){
        return "emailTemplate";
    }
    @RequestMapping("/sendMail")
    @Log(title = "邮件发送")
    public Msg sendEmail() throws IOException {
        emailService.sendSimpleEmail("2237544736@qq.com","simple","content");
        emailService.sendHtmlEmail("2237544736@qq.com","html","<a style='color:red'>1231241</a>");
        emailService.sendAttachmentsEmail("2237544736@qq.com","file","content","classpath:static/file/111.docx");

        emailService.sendInlineResourceEmail("2237544736@qq.com","innerSource","content","classpath:templates/emailTemplate","123");
        return Msg.success();
    }
}
