package com.neo.shiro.web.controller;

import com.neo.enums.ErrorEnum;
import com.neo.enums.OnlineStatus;
import com.neo.model.online.OnlineSession;
import com.neo.model.online.OnlineUser;
import com.neo.shiro.service.OnlineUserService;
import com.neo.shiro.session.OnlineSessionDAO;
import com.neo.util.Response;
import com.neo.util.ShiroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/monitor/online")
public class OnlineUserController {
    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private OnlineSessionDAO onlineSessionDAO;


    @GetMapping
    public String online() {
        return "monitor/online";
    }

    @PostMapping("/forceOut")
    @ResponseBody
    public Response forceOut(@RequestParam("ids[]") List<String> ids, HttpSession session) {
        for (String sessionId : ids) {
            OnlineUser online = onlineUserService.selectOnlineById(sessionId);
            if (online == null) {
                return Response.error("用户已下线");
            }
            OnlineSession onlineSession = (OnlineSession) onlineSessionDAO.readSession(online.getSessionId());
            if (onlineSession == null) {
                return Response.error("用户已下线");
            }
            if (sessionId.equals(ShiroUtil.getSession().getId())) {
                return Response.error("当前登陆用户无法强退");
            }
            onlineSession.setStatus(OnlineStatus.OFF_LINE);
            onlineSessionDAO.update(onlineSession);
            online.setStatus(OnlineStatus.OFF_LINE);
            onlineUserService.saveOnline(online);
        }


        return Response.success();
    }

    @ResponseBody
    @RequestMapping("/users")
    public Response onlineUsers(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, OnlineUser onlineUser) {
        return Response.success(onlineUserService.onlineLists(pageNum, pageSize, onlineUser));
    }
}
