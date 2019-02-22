package com.neo.shiro.web.controller;
import com.neo.model.online.OnlineUser;
import com.neo.shiro.service.OnlineUserService;
import com.neo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/monitor/online")
public class OnlineUserController {
    @Autowired
    private OnlineUserService onlineUserService;

    @GetMapping
    public String online() {
        return "monitor/online";
    }

    @PostMapping("/forceOut")
    @ResponseBody
    public Response forceOut(@RequestParam("ids[]") List<String> ids) {
        ids.forEach(id -> onlineUserService.forceLogout(id));
        return Response.success();
    }

    @ResponseBody
    @RequestMapping("/users")
    public Response onlineUsers(@RequestParam(defaultValue = "0") int pageNum, @RequestParam(defaultValue = "10") int pageSize, OnlineUser onlineUser) {
        return Response.success(onlineUserService.onlineLists(pageNum, pageSize, onlineUser));
    }
}
