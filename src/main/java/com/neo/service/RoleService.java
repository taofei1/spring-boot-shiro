package com.neo.service;

import com.neo.entity.Role;
import com.neo.exception.BusinessException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    Page<Role> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Role user);
    Page<Role> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<Role> findAll();
    List<Role> findByState(Integer state);
    List<Role> findByIds(List<Integer> ids);
    void delete(Integer id) throws BusinessException;
    void delete(List<Integer> ids) throws BusinessException;
    Role findById(Integer id);
    Role save(Role role);
    List<Role> findByPermissionId( Integer id);
}
