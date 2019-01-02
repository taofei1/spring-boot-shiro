package com.neo.service.impl;

import com.neo.dao.DateIntervalDao;
import com.neo.entity.DateInterval;
import com.neo.service.DateIntervalService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service("dateIntervalService")
public class DateIntervalServiceImpl implements DateIntervalService {
    @Autowired
    private DateIntervalDao dateIntervalDao;
    @Override
    public Page<DateInterval> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, DateInterval interval) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("singleDate", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("dateInterval", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<DateInterval> ex = Example.of(interval, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        return dateIntervalDao.findAll(ex,pageable);
    }

    @Override
    public Page<DateInterval> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return dateIntervalDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));

    }

    @Override
    public List<DateInterval> findAll() {
        return dateIntervalDao.findAll();
    }

    @Override
    public List<DateInterval> findAll(List<Integer> ids) {
        return dateIntervalDao.findAll(ids);
    }

    @Override
    public List<DateInterval> findAll(DateInterval interval) {
        Example<DateInterval> ex = Example.of(interval);
        return dateIntervalDao.findAll(ex);
    }

    @Override
    public void delete(Integer id) {
         dateIntervalDao.delete(id);
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public DateInterval findById(Integer id) {
        return dateIntervalDao.findOne(id);
    }

    @Override
    public DateInterval save(DateInterval server) {
        return dateIntervalDao.save(server);
    }

    @Override
    public List<DateInterval> findByStatus(int i) {
        return dateIntervalDao.findByStatus(i);
    }
}
