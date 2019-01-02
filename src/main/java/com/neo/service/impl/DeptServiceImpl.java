package com.neo.service.impl;

import com.neo.dao.DeptDao;
import com.neo.entity.Dept;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.service.DeptService;
import com.neo.service.UserInfoService;
import com.neo.util.PageableUtil;
import com.neo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private UserInfoService userInfoService;
    @Override
    public Page<Dept> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Dept dept) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("location", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("state", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Dept> ex = Example.of(dept, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);


        return deptDao.findAll(ex,pageable);
    }

    @Override
    public Page<Dept> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return deptDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));

    }

    @Override
    public List<Dept> findAll() {
        return deptDao.findAll();
    }
    @Override
    public List<Dept> findByState(Integer state) {
        return deptDao.findByState(state);
    }
    @Override
    public List<Dept> findAll(List<Integer> ids) {
        if(ids==null||ids.size()<1){
            return null;
        }
        return deptDao.findAll(ids);
    }

    @Override
    public void delete(Integer id) throws BusinessException {
        Dept dept=deptDao.findOne(id);
        if(StringUtils.isNull(dept)){
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND);
        }
        dept.getUserInfoList().forEach((user)->{
            user.setDeptList(null);
        //    userInfoService.save(user);

        });
        deptDao.delete(dept);
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) throws BusinessException {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public Dept findById(Integer id) {
        return deptDao.findOne(id);
    }

    @Override
    public Dept save(Dept dept) {
        return deptDao.save(dept);
    }
}
