package com.neo.shiro.web.session;

import com.neo.model.online.OnlineSession;
import com.neo.model.online.OnlineUser;
import com.neo.util.DateUtils;
import com.neo.util.JacksonUtils;
import com.neo.util.ShiroConstants;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;

public class OnlineWebSessionManager extends DefaultWebSessionManager {
    private static final Logger log = LoggerFactory.getLogger(OnlineWebSessionManager.class);
    @Resource(name = "shiroEhcache")
    private EhCacheManager cacheManager;

    @Override
    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) throws InvalidSessionException {
        super.setAttribute(sessionKey, attributeKey, value);
        if (value != null && needMarkAttributeChanged(attributeKey)) {
            Object obj = doGetSession(sessionKey);
            OnlineSession onlineSession = null;
            if (obj instanceof OnlineSession) {
                onlineSession = (OnlineSession) obj;
            } else {
                try {
                    String json = JacksonUtils.obj2json(obj);
                    onlineSession = JacksonUtils.json2pojo(json, OnlineSession.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            onlineSession.markAttributeChanged();
        }
    }

    private boolean needMarkAttributeChanged(Object attributeKey) {
        if (attributeKey == null) {
            return false;
        }
        String attributeKeyStr = attributeKey.toString();
        // 优化 flash属性没必要持久化
        if (attributeKeyStr.startsWith("org.springframework")) {
            return false;
        }
        if (attributeKeyStr.startsWith("javax.servlet")) {
            return false;
        }
        if (attributeKeyStr.equals(ShiroConstants.CURRENT_USERNAME)) {
            return false;
        }
        return true;
    }

    @Override
    public Object removeAttribute(SessionKey sessionKey, Object attributeKey) throws InvalidSessionException {
        Object removed = super.removeAttribute(sessionKey, attributeKey);
        if (removed != null) {
            OnlineSession s = (OnlineSession) doGetSession(sessionKey);
            s.markAttributeChanged();
        }

        return removed;
    }

    /**
     * 验证session是否有效 用于删除过期session
     */
    @Override
    public void validateSessions() {
        if (log.isInfoEnabled()) {
            log.info("invalidation sessions...");
        }
        Cache cache = cacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        Collection values = cache.values();
        int invalidCount = 0;

        int timeout = (int) this.getGlobalSessionTimeout();
        Date expiredDate = DateUtils.addMilliseconds(new Date(), 0 - timeout);
        Iterator iterator = values.iterator();
        List<OnlineUser> expiredList = new ArrayList<>();
        while (iterator.hasNext()) {
            OnlineUser onlineUser = (OnlineUser) iterator.next();

            if (onlineUser.getExpireTime() <= expiredDate.getTime()) {
                expiredList.add(onlineUser);
            }
        }
        // 批量过期删除
        List<String> needOfflineIdList = new ArrayList<>();
        for (OnlineUser userOnline : expiredList) {
            try {
                SessionKey key = new DefaultSessionKey(userOnline.getSessionId());
                Session session = retrieveSession(key);
                if (session != null) {
                    throw new InvalidSessionException();
                }
            } catch (InvalidSessionException e) {
                if (log.isDebugEnabled()) {
                    boolean expired = (e instanceof ExpiredSessionException);
                    String msg = "Invalidated session with id [" + userOnline.getSessionId() + "]"
                            + (expired ? " (expired)" : " (stopped)");
                    log.debug(msg);
                }
                invalidCount++;
                needOfflineIdList.add(userOnline.getSessionId());
            }

        }
        if (needOfflineIdList.size() > 0) {
            try {
                needOfflineIdList.forEach((id) -> cache.remove(id));
            } catch (Exception e) {
                log.error("batch delete db session error.", e);
            }
        }

        if (log.isInfoEnabled()) {
            String msg = "Finished invalidation session.";
            if (invalidCount > 0) {
                msg += " [" + invalidCount + "] sessions were stopped.";
            } else {
                msg += " No sessions were stopped.";
            }
            log.info(msg);
        }

    }

    @Override
    protected Collection<Session> getActiveSessions() {
        throw new UnsupportedOperationException("getActiveSessions method not supported");
    }
}
