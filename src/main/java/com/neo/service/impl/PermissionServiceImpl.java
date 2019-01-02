package com.neo.service.impl;

import com.neo.dao.PermissionDao;
import com.neo.entity.Role;
import com.neo.entity.RolePermission;
import com.neo.service.PermissionService;
import com.neo.service.RoleService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleService roleService;
    @Override
    public Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, RolePermission sysPermission) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("parentId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("permission", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("resourceType", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("url", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<RolePermission> ex = Example.of(sysPermission, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);


        return permissionDao.findAll(ex,pageable);
    }

    @Override
    public Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
       return permissionDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));
    }

    @Override
    public Page<RolePermission> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, List<Integer> ids) {
        Pageable pageable=PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        Page<RolePermission> result=permissionDao.findAll((root, query, cb) -> {
            List<Predicate> list=new ArrayList<>();
            if(ids!=null&&ids.size()>0){
                CriteriaBuilder.In<Integer> in=cb.in(root.get("id"));
                for(Integer id:ids){
                    in.value(id);
                }
                list.add(in);
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));

        },pageable);
        return result;
    }

    @Override
    public List<RolePermission> findAll() {
        return permissionDao.findAll();
    }

    @Override
    public List<RolePermission> findAll(List<Integer> ids) {
        return permissionDao.findAll(ids);
    }


    @Override
    public void delete(Integer id) {
        RolePermission permission=permissionDao.findOne(id);
        List<Role> roles=roleService.findByPermissionId(permission.getId());
        roles.forEach((role)->{
            role.getPermissions().remove(permission);
        });
        permissionDao.delete(permission);

    }
    @Transactional
    @Override
    public void delete(List<Integer> ids) {
        for(Integer id:ids){
            delete(id);

        }

    }

    @Override
    public RolePermission findById(Integer id) {
        RolePermission permission= permissionDao.findOne(id);
       // System.out.println(permission.getRoles());
        return permission;
    }

    @Override
    public RolePermission save(RolePermission sysPermission) {
        if(sysPermission.getId()!=null){
            RolePermission permission=permissionDao.findOne(sysPermission.getId());
            sysPermission.setIcon(permission.getIcon());
        }
        return permissionDao.save(sysPermission);
    }

}
