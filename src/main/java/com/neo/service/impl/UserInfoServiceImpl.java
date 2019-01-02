package com.neo.service.impl;

import com.neo.dao.UserInfoDao;
import com.neo.entity.UserInfo;
import com.neo.service.UserInfoService;
import com.neo.util.PageableUtil;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;

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


}
