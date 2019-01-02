package com.neo.mapper;

import com.neo.pojo.OperLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperLogMapper {
    /**
     * 根据条件查询
     * @param operLog
     * @return
     */
    List<OperLog> selectOperLogList(OperLog operLog);
    /**
     * 插入一条操作日志
     * @param operLog
     */
    void insertOperlog(OperLog operLog);

    /**
     * 批量删除
     * @param ids
     */
    void deleteOperLogByIds(String[] ids);

    /**
     * 删除一条
     * @param id
     */
    void deleteOne(Long id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    OperLog selectOperLogById(Long id);

    /**
     * 清空日志表
     */
    void cleanOperLog();
}
