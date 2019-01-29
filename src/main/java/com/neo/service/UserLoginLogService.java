package com.neo.service;

import com.neo.dto.LoginLogDTO;
import com.neo.entity.LoginLog;
import com.neo.exception.BusinessException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserLoginLogService {
    Page<LoginLogDTO> findAll(int pageNum, int pageSize, String sortName, Integer type, LoginLog loginLog);
    Page<LoginLogDTO> findAll(int pageNum, int pageSize, String sortName, Integer type );
    List<LoginLogDTO> findAll();
    List<LoginLogDTO> findAll(List<Integer> ids);
    List<LoginLogDTO> findAll(LoginLog loginLog);
    void delete(Integer id) throws BusinessException;
    void delete(String ids) throws BusinessException;
    void delete(List<Integer> ids) throws BusinessException;
    LoginLogDTO findById(Integer id);
    LoginLogDTO save(LoginLog loginLog);

    void clean();
}
