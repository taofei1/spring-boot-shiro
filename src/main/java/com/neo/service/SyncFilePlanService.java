package com.neo.service;

import com.neo.entity.SyncFilePlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SyncFilePlanService {
    Page<SyncFilePlan> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, SyncFilePlan syncFilePlan);
    Page<SyncFilePlan> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<SyncFilePlan> findAll();
    List<SyncFilePlan> findAll(List<Integer> ids);
    List<SyncFilePlan> findAll(SyncFilePlan syncFilePlan);
    void delete(Integer id);
    void delete(List<Integer> ids);
    SyncFilePlan findById(Integer id);
    SyncFilePlan save(SyncFilePlan syncFilePlan);
     SyncFilePlan updateStatus(SyncFilePlan syncFilePlan) ;
    int updateIsConcurrent(Integer id,Integer status);

}
