package com.neo.service.impl;

import com.neo.dao.RoleDao;
import com.neo.entity.Role;
import com.neo.exception.BusinessException;
import com.neo.enums.ErrorEnum;
import com.neo.service.RoleService;
import com.neo.service.UserInfoService;
import com.neo.util.PageableUtil;
import com.neo.util.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserInfoService userInfoService;
    @Override
    public Page<Role> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Role role) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("role", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Role> ex = Example.of(role, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);


        return roleDao.findAll(ex,pageable);
    }

    @Override
    public Page<Role> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return roleDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));

    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public List<Role> findByState(Integer state) {
        return roleDao.findByState(state);
    }

    @Override
    public List<Role> findByIds(List<Integer> ids) {
        if(ids==null||ids.size()<1){
            return null;
        }
        return roleDao.findAll(ids);
    }

    @Override
    public void delete(Integer id) throws BusinessException {
        Role role=roleDao.findOne(id);
        if(StringUtils.isNull(role)){
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND);
        }
        //解除关联关系
      //  role.setPermissions(null);
        role.getUserInfos().forEach((user)->{
            user.setRoleList(null);
        //    userInfoService.save(user);
        });
        roleDao.delete(role);
    }
    @Transactional
    @Override
    public void delete(List<Integer> ids) throws BusinessException {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public Role findById(Integer id) {
        return roleDao.findOne(id);
    }

    @Override
    public Role save(Role role) {
        return roleDao.save(role);
    }

    @Override
    public List<Role> findByPermissionId(Integer id) {
        return roleDao.findByPermissionId(id);
    }

}
