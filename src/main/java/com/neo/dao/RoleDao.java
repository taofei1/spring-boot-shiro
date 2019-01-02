package com.neo.dao;

import com.neo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleDao extends JpaRepository<Role,Integer>,QueryByExampleExecutor<Role> {
    List<Role> findByState(Integer state);
    @Query(value = "SELECT a.* FROM sys_role a,sys_permission b,sys_role_permission c WHERE a.id=c.role_id AND b.id=c.permission_id AND b.id=?1",nativeQuery = true)
    List<Role> findByPermissionId( Integer id);
}
