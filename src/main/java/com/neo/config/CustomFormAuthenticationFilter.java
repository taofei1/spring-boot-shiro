package com.neo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.entity.UserInfo;
import com.neo.util.Msg;
import com.neo.util.ShiroFilterUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter{
    private static final Logger log = LoggerFactory.getLogger(CustomFormAuthenticationFilter.class);
    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String kickoutUrl; // 踢出后到的地址
    private boolean kickoutAfter = false; // 踢出之前登录的/之后登录的用户 默认false踢出之前登录的用户
    private int maxSession = 1; // 同一个帐号最大会话数 默认1
    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // 设置Cache的key的前缀
    public void setCacheManager(CacheManager cacheManager) {
        //必须和ehcache缓存配置中的缓存name一致
        this.cache = cacheManager.getCache("shiro-activeSessionCache");
    }
    public String getCaptchaParam() {

        return captchaParam;

    }

    protected String getCaptcha(ServletRequest request) {

        return WebUtils.getCleanParam(request, getCaptchaParam());

    }
    @Override
    protected AuthenticationToken createToken(

            ServletRequest request, ServletResponse response) {

        String username = getUsername(request);

        String password = getPassword(request);

        String captcha = getCaptcha(request);

        boolean rememberMe = isRememberMe(request);

        String host = getHost(request);
        return new UsernamePasswordCaptchaToken(username,
                password.toCharArray(), rememberMe, host, captcha);

    }
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
    {
        if (isLoginRequest(request, response))
        {
            if (isLoginSubmission(request, response))
            {
                //本次用户登陆账号
              /*  String account = this.getUsername(request);

                Subject subject = this.getSubject(request, response);
                //之前登陆的用户
                UserInfo user = (UserInfo) subject.getPrincipal();
                //如果两次登陆的用户不一样，则先退出之前登陆的用户
                if (account != null && user != null && !account.equals(user.getUsername())&& !account.equals(user.getEmail()))
                {
                    subject.logout();
                }*/
                return false;
            }
        }

        return super.isAccessAllowed(request, response, mappedValue);
    }


}
