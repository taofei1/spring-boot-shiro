package com.neo.service;

import com.neo.entity.RolePermission;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PermissionService {
    Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, RolePermission user);
    Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, List<Integer> ids);
    List<RolePermission> findAll();
    List<RolePermission> findAll(List<Integer> ids);
    void delete(Integer id);
    void delete(List<Integer> ids);
    RolePermission findById(Integer id);
    RolePermission save(RolePermission sysPermission);
}
