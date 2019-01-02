package com.neo.web;

import com.neo.annotation.Log;
import com.neo.enums.OperateType;
import com.neo.model.MailConfig;
import com.neo.service.EmailService;
import com.neo.util.MailUtil;
import com.neo.util.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MailConfigController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EmailService emailService;
    @GetMapping("/getMailConfig")
    public Msg getConfigPara() {
        try {
            return Msg.success().addData(MailUtil.getMailConfig());
        } catch (IOException e) {
            return Msg.fail().addData("获取配置信息失败！错误信息："+e.toString());
        }
    }
    @PostMapping("/setConfig")
    @Log(title = "email信息配置",businessType = OperateType.UPDATE)
    public Msg setConfigPara(String username,String password,String host,Integer port){
        MailConfig mailConfig=new MailConfig(username,password,host,port);
        log.info(mailConfig.toString());
        try {
            MailUtil.setConfig(mailConfig);
            return Msg.success();
        } catch (IOException e) {
            return Msg.fail().addData("设置配置信息失败!错误信息"+e.toString());
        }

    }
}
