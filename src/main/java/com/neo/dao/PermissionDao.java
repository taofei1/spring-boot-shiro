package com.neo.dao;

import com.neo.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDao extends JpaRepository<RolePermission,Integer>,QueryByExampleExecutor<RolePermission>, JpaSpecificationExecutor<RolePermission> {
}
