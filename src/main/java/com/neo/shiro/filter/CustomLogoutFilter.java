package com.neo.shiro.filter;

import com.neo.constants.GlobalConstants;
import com.neo.entity.UserInfo;
import com.neo.enums.SignType;
import com.neo.factory.AsyncTaskFactory;
import com.neo.thread.AsyncThreadPool;
import com.neo.util.ExceptionUtil;
import com.neo.util.MessageUtil;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 退出过滤器
 *
 * @author ruoyi
 */
@Slf4j
public class CustomLogoutFilter extends LogoutFilter {

    /**
     * 退出后重定向的地址
     */
    private String loginUrl;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        UserInfo user = ShiroUtil.getSysUser();
        String username = "";
        if (StringUtils.isNotNull(user)) {
            username = user.getUsername();
            // 记录用户退出日志
        }
        // 退出登录
        Subject subject = getSubject(request, response);
        String redirectUrl = getRedirectUrl(request, response, subject);
        try {

            subject.logout();
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGOUT, GlobalConstants.SUCCESS, MessageUtil.message("user.logout.success")));
        } catch (SessionException ise) {
            log.error("logout fail.", ise);
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGOUT, GlobalConstants.FAIL, MessageUtil.message("user.logout.exception") + ExceptionUtil.getStackTraceInfo(ise)));

        }
        issueRedirect(request, response, redirectUrl);

        return false;
    }

    /**
     * 退出跳转URL
     */
    @Override
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
        String url = getLoginUrl();
        if (StringUtils.isNotEmpty(url)) {
            return url;
        }
        return super.getRedirectUrl(request, response, subject);
    }
}
