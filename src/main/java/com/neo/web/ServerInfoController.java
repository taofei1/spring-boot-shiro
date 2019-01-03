package com.neo.web;

import com.neo.exception.ErrorEnum;
import com.neo.model.server.Server;
import com.neo.util.Response;
import com.neo.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/server")
@Slf4j
public class ServerInfoController {
    @Autowired
     private SimpMessagingTemplate template;

        @RequiresPermissions("monitor:server")
        @GetMapping()
        public String server(ModelMap mmap) throws Exception
        {
            Server server = new Server();
            server.copyTo();
            log.info("server:{}",server);
            mmap.put("server", server);
            return   "monitor/server";
        }




        //广播推送消息
        @Scheduled(fixedRate = 3000)
        public void sendTopicMessage() {
            Server server = new Server();
            server.copyTo();
            this.template.convertAndSend("/websocket",server);
        }




}
