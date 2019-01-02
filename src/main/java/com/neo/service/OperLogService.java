package com.neo.service;

import com.neo.exception.BusinessException;
import com.neo.pojo.OperLog;

import java.util.List;

public interface OperLogService {
    void insertOperlog(OperLog operLog) throws BusinessException;
    void deleteOperLogByIds(String ids) throws BusinessException;
    List<OperLog> selectList(Integer pageNum,Integer pageSize,OperLog operLog);
    void cleanOperLog();
    OperLog getOne(Long id);
}
