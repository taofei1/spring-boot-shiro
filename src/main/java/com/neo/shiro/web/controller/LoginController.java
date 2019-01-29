package com.neo.shiro.web.controller;

import com.github.pagehelper.util.StringUtil;
import com.neo.enums.ErrorEnum;
import com.neo.util.Response;
import com.neo.util.ServletUtils;
import com.neo.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        // �����Ajax���󣬷���Json�ַ�����
        if (ServletUtils.isAjaxRequest(request)) {
            return ServletUtils.renderString(response, "{\"code\":\"1\",\"msg\":\"δ��¼���¼��ʱ�������µ�¼\"}");
        }

        return "login";
    }

    @PostMapping("/login")
    public String ajaxLogin(String username, String password, Boolean rememberMe, Map map) {
        if (StringUtils.isNull(rememberMe)) {
            rememberMe = false;
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return "redirect:/index";
        } catch (AuthenticationException e) {
            String msg = "�û����������";
            if (StringUtil.isNotEmpty(e.getMessage())) {
                msg = e.getMessage();
            }
            map.put("msg", msg);
            return "login";

        }
    }

    @GetMapping("/unauth")
    public String unauth() {
        return "403";
    }
}
