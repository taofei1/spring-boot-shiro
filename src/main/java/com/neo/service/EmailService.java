package com.neo.service;


import java.io.IOException;

public interface EmailService {
        /**
         * 发送简单邮件
         * @param to
         * @param subject
         * @param content
         */
         void sendSimpleEmail(String to, String subject, String content);
        /**
         * 发送html格式邮件
         * @param to
         * @param subject
         * @param content
         */
         void sendHtmlEmail(String to, String subject, String content);
        /**
         * 发送带附件的邮件
         * @param to
         * @param subject
         * @param content
         * @param filePath
         */
         void sendAttachmentsEmail(String to, String subject, String content, String filePath) throws IOException;
        /**
         * 发送带静态资源的邮件
         * @param to
         * @param subject
         * @param content
         * @param rscPath
         * @param rscId
         */
         void sendInlineResourceEmail(String to, String subject, String content, String rscPath, String rscId);


}
