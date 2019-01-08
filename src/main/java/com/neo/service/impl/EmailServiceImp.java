package com.neo.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.neo.entity.UserInfo;
import com.neo.model.MailConfig;
import com.neo.service.EmailService;
import com.neo.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;


@Component    //记得开启，不然启动时不会装载

public class EmailServiceImp implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private JavaMailSender javaMailSender= MailUtil.javaMailSender();//spring 提供的邮件发送类


    public EmailServiceImp() throws IOException {
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();//创建简单邮件消息
        try {
            message.setFrom(MailUtil.getMailConfig().getUsername());//设置发送人
        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setTo(to);//设置收件人

       /* String[] adds = {"xxx@qq.com","yyy@qq.com"}; //同时发送给多人
        message.setTo(adds);*/

        message.setSubject(subject);//设置主题
        message.setText(content);//设置内容
        try {
            javaMailSender.send(message);//执行发送邮件
            logger.info("简单邮件已经发送。");
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常！", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(MailUtil.getMailConfig().getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            logger.info("html邮件发送成功");
        } catch (MessagingException e) {
            logger.error("发送html邮件时发生异常！", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendAttachmentsEmail(String to, String subject, String content, String filePath)   {
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(MailUtil.getMailConfig().getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);// true表示这个邮件是有附件的
            File file1 = ResourceUtils.getFile(filePath);

            FileSystemResource file = new FileSystemResource(file1);//创建文件系统资源
            System.out.println(file.getFilename());
            //
            helper.addAttachment(file.getFilename(), file);//添加附件

            javaMailSender.send(message);
            logger.info("带附件的邮件已经发送。");
        } catch (MessagingException e) {
            logger.error("发送带附件的邮件时发生异常！", e);
        } catch (FileNotFoundException e) {
            logger.error("文件未找到！", e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendInlineResourceEmail(String to, String subject, String content, String rscPath, String rscId) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(MailUtil.getMailConfig().getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            File file1 = ResourceUtils.getFile(rscPath);
            FileSystemResource res = new FileSystemResource(file1);

            //添加内联资源，一个id对应一个资源，最终通过id来找到该资源
            helper.addInline(rscId, res);//添加多个图片可以使用多条 <img src='cid:" + rscId + "' > 和 helper.addInline(rscId, res) 来实现

            javaMailSender.send(message);
            logger.info("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            logger.error("发送嵌入静态资源的邮件时发生异常！", e);
        } catch (FileNotFoundException e) {
            logger.error("文件未找到异常！", e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}