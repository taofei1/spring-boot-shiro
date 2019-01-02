package com.neo.service;

import com.neo.entity.Dept;
import com.neo.exception.BusinessException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeptService {
    Page<Dept> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, Dept dept);
    Page<Dept> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<Dept> findAll();
    List<Dept> findByState(Integer state);
    List<Dept> findAll(List<Integer> ids);
    void delete(Integer id) throws BusinessException;
    void delete(List<Integer> ids) throws BusinessException;
    Dept findById(Integer id);
    Dept save(Dept dept);
}
