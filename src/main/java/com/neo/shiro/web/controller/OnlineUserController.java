package com.neo.shiro.web.controller;

import com.neo.model.online.OnlineUser;
import com.neo.util.Response;
import com.neo.util.ShiroConstants;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/online")
public class OnlineUserController {


    @ResponseBody
    @RequestMapping("/users")
    public Response onlineUsers(int pageNum, int pageSize, OnlineUser onlineUser) {
        //System.out.println(cacheManager.getCache(ShiroConstants.SESSIONS_CACHE).values());
        return Response.success();
    }
}
