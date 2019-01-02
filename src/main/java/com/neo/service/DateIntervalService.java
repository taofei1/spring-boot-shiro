package com.neo.service;

import com.neo.entity.DateInterval;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DateIntervalService {
    Page<DateInterval> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, DateInterval interval);
    Page<DateInterval> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<DateInterval> findAll();
    List<DateInterval> findAll(List<Integer> ids);
    List<DateInterval> findAll(DateInterval interval);
    void delete(Integer id);
    void delete(List<Integer> ids);
    DateInterval findById(Integer id);
    DateInterval save(DateInterval server);

    List<DateInterval> findByStatus(int i);
}
