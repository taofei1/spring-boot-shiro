package com.neo.service.impl;

import com.neo.constants.GlobalConstants;
import com.neo.dao.UserInfoDao;
import com.neo.entity.UserInfo;
import com.neo.enums.SignType;
import com.neo.enums.UserStatus;
import com.neo.exception.*;
import com.neo.factory.AsyncTaskFactory;
import com.neo.service.UserInfoService;
import com.neo.shiro.service.PasswordService;
import com.neo.thread.AsyncThreadPool;
import com.neo.util.*;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;
    @Autowired
    private PasswordService passwordService;

    @Override
    public UserInfo findByUsername(String username) {
        return userInfoDao.findByUsername(username);
    }

    @Override
    public UserInfo findByEmail(String username) {
        return userInfoDao.findByEmail(username);
    }

    @Override
    public Page<UserInfo> findAll(int pageNum, int pageSize,String sortName,Integer type) {

        return userInfoDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));
    }

    @Override
    public Page<UserInfo> findAll(int pageNum, int pageSize, String sortName, Integer type, UserInfo user) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("uid", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("gender", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("birthday", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("mobile", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.exact())
                .withIgnorePaths("password");
        Example<UserInfo> ex = Example.of(user, matcher);
        Pageable pageable=PageableUtil.getPageable(pageNum,pageSize,sortName,type);


        return userInfoDao.findAll(ex,pageable);

    }

    @Override
    public void delete(Integer id) {
        userInfoDao.delete(id);
    }
    @Override
    @Transactional
    public void delete(List<Integer> ids) {

        for(Integer id:ids){
            delete(id);
        }
    }
  /*  @Override
    public void delete(List<UserInfo> list) {
        userInfoDao.delete(list);
    }*/

    @Override
   // @Cacheable(value = CACHE_KEY, key = "#id")
    public UserInfo findById(Integer id) {
        return userInfoDao.findOne(id);
    }

    @Override
   // @CachePut(value = CACHE_KEY, key = "#userInfo.uid")
    public UserInfo save(UserInfo userInfo) {
        return userInfoDao.save(userInfo);
    }

    @Override
    public UserInfo login(String username, String password) {
        // 验证码校验
        if (!StringUtils.isNull(ServletUtils.getRequest().getAttribute(ShiroConstants.CURRENT_CAPTCHA))) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.captcha.error")));
            throw new CaptchaException();
        }
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.username.password.empty")));
            throw new EmptyUsernameOrPasswordException();
        }


        // 用户名不在指定范围内 错误
        if (!username.matches(GlobalConstants.USERNAME_PATTERN) && !username.matches(GlobalConstants.EMAIL_PATTERN)) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.username.not.match")));
            throw new UsernameNotMatchException();
        }

        // 查询用户信息
        UserInfo user = userInfoDao.findByUsername(username);

        if (user == null && maybeEmail(username)) {
            user = userInfoDao.findByEmail(username);
        }

        if (user == null) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.username.not.exist")));
            throw new UnknownAccountException();
        }


        if (UserStatus.DISABLE.getCode().equals(user.getState())) {
            AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.FAIL, MessageUtil.message("user.account.disable")));
            throw new DisabledAccountException();
        }

        passwordService.validate(user, password);

        AsyncThreadPool.getInstance().execute(AsyncTaskFactory.recordLoginAndLogout(username, SignType.LOGIN, GlobalConstants.SUCCESS, MessageUtil.message("user.login.success")));
        return user;
    }


    private boolean maybeEmail(String username) {
        if (!username.matches(GlobalConstants.EMAIL_PATTERN)) {
            return false;
        }
        return true;
    }

    private boolean maybeUsername(String username) {
        if (!username.matches(GlobalConstants.USERNAME_PATTERN)) {
            return false;
        }
        return true;
    }


}
