package com.neo.web;

import com.neo.model.server.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * ·þÎñÆ÷¼à¿Ø
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/server")
@Slf4j
public class ServerInfoController {

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


}
