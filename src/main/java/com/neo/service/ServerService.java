package com.neo.service;

import com.neo.entity.Server;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServerService {
    Page<Server> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Server server);
    Page<Server> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<Server> findAll();
    List<Server> findAll(List<Integer> ids);
    List<Server> findAll(Server server);
    void delete(Integer id);
    void delete(List<Integer> ids);
    Server findById(Integer id);
    Server save(Server server);
}
