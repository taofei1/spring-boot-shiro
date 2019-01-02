package com.neo.service.impl;

import com.neo.dao.ServerDao;
import com.neo.entity.Server;
import com.neo.service.ServerService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service("serverService")
public class ServerServiceImpl implements ServerService {
    @Autowired
    private ServerDao serverDao;
    @Override
    public Page<Server> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Server server) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("ip", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("port", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("password", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Server> ex = Example.of(server, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        return serverDao.findAll(ex,pageable);
    }

    @Override
    public Page<Server> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {

        return serverDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));
    }

    @Override
    public List<Server> findAll() {
        return serverDao.findAll();
    }

    @Override
    public List<Server> findAll(List<Integer> ids) {
        return serverDao.findAll(ids);
    }

    @Override
    public List<Server> findAll(Server server) {
        Example<Server> ex = Example.of(server);
        return serverDao.findAll(ex);
    }

    @Override
    public void delete(Integer id) {
        serverDao.delete(id);
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public Server findById(Integer id) {
        return serverDao.findOne(id);
    }

    @Override
    public Server save(Server server) {
        return serverDao.save(server);
    }
}
