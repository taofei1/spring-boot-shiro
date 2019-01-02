package com.neo.dao;

import com.neo.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserLoginLogRepo extends JpaRepository<LoginLog,Integer> {
    @Transactional
    @Modifying
    @Query(value = "truncate table sys_login_log",nativeQuery = true)
    void clean();
}
