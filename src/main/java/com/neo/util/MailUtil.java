package com.neo.util;

import com.neo.model.MailConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Slf4j
public class MailUtil {
    public static MailConfig getConfig() throws IOException {

        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = MailUtil.class.getClassLoader().getResourceAsStream("email-config.properties");
        // 使用properties对象加载输入流
        properties.load(in);

        return new MailConfig(properties.getProperty("email.username"),properties.getProperty("email.password"),properties.getProperty("email.host"),Integer.parseInt( properties.getProperty("email.port")));


    }

    public static void setMailConfig(MailConfig mailConfig) throws IOException {
        String path = System.getProperty("user.dir");
        String profilepath = path + "/src/main/resources/email-config.properties";//
        log.info(profilepath);
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = MailUtil.class.getClassLoader().getResourceAsStream("email-config.properties");
        // 使用properties对象加载输入流
        properties.load(in);
        properties.setProperty("email.port",mailConfig.getPort().toString());
        properties.setProperty("email.host",mailConfig.getHost());
        properties.setProperty("email.username",mailConfig.getUsername());
        properties.setProperty("email.password",mailConfig.getPassword());

        FileOutputStream fileOutputStream=new FileOutputStream(profilepath);
        properties.store(fileOutputStream,"updated");
        fileOutputStream.flush();
        in.close();
        fileOutputStream.close();

    }

    public static JavaMailSenderImpl javaMailSender() throws IOException {
        MailConfig mailConfig=getConfig();
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setPort(mailConfig.getPort());
        javaMailSender.setHost(mailConfig.getHost());
        javaMailSender.setUsername(mailConfig.getUsername());
        javaMailSender.setPassword(mailConfig.getPassword());
        //获取key对应的value值
//       Properties properties = new Properties();
//       properties.setProperty("mail.host", "smtp.qq.com");
//       properties.setProperty("mail.transport.protocol", "smtp");
//       properties.setProperty("mail.smtp.auth", "true");
//       properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//       properties.setProperty("mail.smtp.port", "465");
//       properties.setProperty("mail.smtp.socketFactory.port", "465");
//
//       javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    public static MailConfig getMailConfig() throws IOException {
        return MailUtil.getConfig();
    }

    public static void setConfig(MailConfig mailConfig) throws IOException {
        MailUtil.setMailConfig(mailConfig);
    }
}
