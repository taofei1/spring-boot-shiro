package com.neo.shiro.session;

import com.neo.enums.OnlineStatus;
import com.neo.factory.AsyncTaskFactory;
import com.neo.model.online.OnlineSession;
import com.neo.model.online.OnlineUser;
import com.neo.thread.AsyncThreadPool;
import com.neo.util.ShiroConstants;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;

public class OnlineSessionDAO extends EnterpriseCacheSessionDAO {
    /**
     * 上次同步数据库的时间戳
     */
    private static final String LAST_SYNC_DB_TIMESTAMP = OnlineSessionDAO.class.getName() + "LAST_SYNC_DB_TIMESTAMP";
    /**
     * 同步session到内存的周期 单位为毫秒（默认1分钟）
     */
    private int syncPeriod = 30000;
    @Resource(name = "shiroEhcache")
    private EhCacheManager ehCacheManager;
    private Cache cache;
    @Autowired
    private OnlineSessionFactory onlineSessionFactory;

    public OnlineSessionDAO() {
        super();
    }


    public OnlineSessionDAO(long expireTime) {
        super();
    }

    public void setSyncPeriod(int syncPeriod) {
        this.syncPeriod = syncPeriod;
    }

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return ShiroSession
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        cache = ehCacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        OnlineUser user = (OnlineUser) cache.get(sessionId);
        if (user == null) {
            return null;
        }
        return onlineSessionFactory.createSession(user);
    }

    /**
     * 更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
     */
    public void syncToMemory(OnlineSession onlineSession) {
        cache = ehCacheManager.getCache(ShiroConstants.SESSIONS_CACHE);

        //获取上次同步时间
        Date lastSyncTimestamp = (Date) onlineSession.getAttribute(LAST_SYNC_DB_TIMESTAMP);
        if (lastSyncTimestamp != null) {
            //默认需要同步
            boolean needSync = true;

            long deltaTime = onlineSession.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < syncPeriod) {
                // 时间差不足 无需同步
                needSync = false;
            }
            boolean isGuest = onlineSession.getUserId() == null || onlineSession.getUserId() == 0L;

            // session 数据变更了 同步
            if (isGuest == false && onlineSession.isAttributeChanged()) {
                needSync = true;
            }

            if (needSync == false) {
                return;
            }
        }
        onlineSession.setAttribute(LAST_SYNC_DB_TIMESTAMP, onlineSession.getLastAccessTime());
        // 更新完后 重置标识
        if (onlineSession.isAttributeChanged()) {
            onlineSession.resetAttributeChanged();
        }
        AsyncThreadPool.getInstance().execute(AsyncTaskFactory.syncSessionToMemory(onlineSession, cache));
    }

    /**
     * 当会话过期/停止（如用户退出时）属性等会调用
     */
    @Override
    protected void doDelete(Session session) {
        cache = ehCacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        OnlineSession onlineSession = (OnlineSession) session;
        if (null == onlineSession) {
            return;
        }
        onlineSession.setStatus(OnlineStatus.OFF_LINE);
        cache.remove(session.getId());
    }
}
