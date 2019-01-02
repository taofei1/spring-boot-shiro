package com.neo.service;

import com.neo.entity.UserInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserInfoService {
    /**通过username查找用户信息;*/
  UserInfo findByUsername(String username);

    UserInfo findByEmail(String username);

    Page<UserInfo> findAll(int pageNum, int pageSize,String sortName,Integer type );
    Page<UserInfo> findAll(int pageNum,int pageSize,String sortName,Integer type,UserInfo user);
   void delete(Integer id);
    void delete(List<Integer> ids);
   //void delete(List<UserInfo> list);
   UserInfo findById(Integer id);
   UserInfo save(UserInfo userInfo);

}