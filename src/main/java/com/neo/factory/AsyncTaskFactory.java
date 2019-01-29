package com.neo.factory;

import com.neo.entity.LoginLog;

import com.neo.enums.SignType;
import com.neo.model.online.OnlineSession;
import com.neo.model.online.OnlineUser;
import com.neo.pojo.OperLog;
import com.neo.quartz.SpringUtil;
import com.neo.service.OperLogService;
import com.neo.service.UserLoginLogService;
import com.neo.util.IPUtils;
import com.neo.util.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;

import java.util.Date;
import java.util.TimerTask;

/**
 * 异步任务产生工厂，都是耗时操作
 */
@Slf4j
public class AsyncTaskFactory {

    /**
     * 同步session到内存
     *
     * @param session 在线用户会话
     * @return 任务task
     */
    public static TimerTask syncSessionToMemory(final OnlineSession session, final Cache cache) {
        return new TimerTask() {
            @Override
            public void run() {
                OnlineUser online = new OnlineUser();
                online.setSessionId(String.valueOf(session.getId()));
                online.setLoginName(session.getLoginName());
                online.setStartTimestamp(session.getStartTimestamp());
                online.setLastAccessTime(session.getLastAccessTime());
                online.setExpireTime(session.getTimeout());
                online.setIpaddr(session.getHost());
                online.setLoginLocation(IPUtils.getLocationByIP(session.getHost()));
                online.setBrowser(session.getBrowser());
                online.setOs(session.getOs());
                online.setStatus(session.getStatus());
                online.setSession(session);
                cache.put(session.getId().toString(), online);

            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final OperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作耗时
                operLog.setOperLocation(IPUtils.getLocationByIP(operLog.getOperIp()));
                try {
                    SpringUtil.getBean(OperLogService.class).insertOperlog(operLog);
                } catch (Exception e) {
                    log.error("日志记录操作出错！" + operLog.toString(), e);

                }
            }
        };
    }

    /**
     * 记录登录登出日志
     *
     * @param username  用户
     * @param signType  登录或者登出
     * @param isSuccess 是否成功
     * @param msg       提示信息
     * @return
     */
    public static TimerTask recordLoginAndLogout(final String username, final SignType signType, final Integer isSuccess, final String msg) {
        return new TimerTask() {
            final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
            final String ip = IPUtils.getRealIP();

            @Override
            public void run() {
                LoginLog loginLog = new LoginLog();
                loginLog.setBrowser(userAgent.getBrowser().getName());
                loginLog.setIp(ip);
                loginLog.setIsSuccess(isSuccess);
                loginLog.setLoginTime(new Date());
                loginLog.setOs(userAgent.getOperatingSystem().getName());
                loginLog.setMsg(msg);
                loginLog.setUsername(username);
                loginLog.setSignType(signType.ordinal());
                try {
                    SpringUtil.getBean(UserLoginLogService.class).save(loginLog);
                } catch (Exception e) {
                    log.error("记录登录登出日志出错！" + loginLog, e);

                }
            }
        };
    }

}
