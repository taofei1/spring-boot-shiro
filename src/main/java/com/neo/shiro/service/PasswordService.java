package com.neo.shiro.service;

import com.neo.constants.GlobalConstants;
import com.neo.entity.UserInfo;
import com.neo.enums.SignType;
import com.neo.exception.UserPasswordNotMatchException;
import com.neo.exception.UserPasswordRetryLimitExceedException;
import com.neo.factory.AsyncTaskFactory;
import com.neo.thread.AsyncThreadPool;
import com.neo.util.MessageUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.MessageUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PasswordService {
    @Autowired
    private CacheManager cacheManager;

    private Cache<String, AtomicInteger> loginRecordCache;

    @Value("${user.password.maxRetryCount}")
    private String maxRetryCount;

    @PostConstruct
    public void init() {
        loginRecordCache = cacheManager.getCache("loginRecordCache");
    }

    public void validate(UserInfo user, String password) {
        String loginName = user.getUsername();

        AtomicInteger retryCount = loginRecordCache.get(loginName);

        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            loginRecordCache.put(loginName, retryCount);
        }
        if (retryCount.incrementAndGet() > Integer.valueOf(maxRetryCount).intValue()) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(loginName, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.password.retry.limit.exceed")));
            throw new UserPasswordRetryLimitExceedException(Integer.valueOf(maxRetryCount).intValue());
        }

        if (!matches(user, password)) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(loginName, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.password.not.match")));
            loginRecordCache.put(loginName, retryCount);
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(loginName);
        }
    }

    public boolean matches(UserInfo user, String newPassword) {
        return user.getPassword().equals(encryptPassword(newPassword, user.getSalt(), 2));
    }

    public void clearLoginRecordCache(String username) {
        loginRecordCache.remove(username);
    }

    public String encryptPassword(String password, String salt, int hashIterations) {

        return new Md5Hash(password, salt, hashIterations).toString();
    }

}
