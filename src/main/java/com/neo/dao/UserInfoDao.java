package com.neo.dao;


import com.neo.entity.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoDao extends JpaRepository<UserInfo,Integer>,QueryByExampleExecutor<UserInfo> {
    /**通过username查找用户信息;*/
     UserInfo findByUsername(String username);

    UserInfo findByEmail(String username);
}