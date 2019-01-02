package com.neo.service.impl;

import com.neo.DTO.LoginLogDTO;
import com.neo.dao.UserLoginLogRepo;
import com.neo.entity.LoginLog;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.service.UserLoginLogService;
import com.neo.util.Dto2Entity;
import com.neo.util.PageableUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoginLogServiceImpl implements UserLoginLogService {
    @Autowired
    private UserLoginLogRepo userLoginLogRepo;
    @Override
    public Page<LoginLogDTO> findAll(int pageNum, int pageSize, String sortName, Integer type, LoginLog loginLog) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("os", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("browser", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("ip", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<LoginLog> ex = Example.of(loginLog, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        Page<LoginLog> page=userLoginLogRepo.findAll(ex,pageable);

        List<LoginLogDTO> dtoList= convertLoginLogListToDTOList(page.getContent());
        Page<LoginLogDTO> newPage=new PageImpl(dtoList,pageable,page.getTotalElements());
        return newPage;

    }

    @Override
    public Page<LoginLogDTO> findAll(int pageNum, int pageSize, String sortName, Integer type) {
        Pageable pageable=PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        Page<LoginLog> page= userLoginLogRepo.findAll(pageable);
        List<LoginLogDTO> dtoList= convertLoginLogListToDTOList(page.getContent());

        Page<LoginLogDTO> newPage=new PageImpl(dtoList,pageable,page.getTotalElements());
        return newPage;
    }

    @Override
    public List<LoginLogDTO> findAll() {
        return convertLoginLogListToDTOList(userLoginLogRepo.findAll());
    }

    @Override
    public List<LoginLogDTO> findAll(List<Integer> ids) {
        return convertLoginLogListToDTOList(userLoginLogRepo.findAll(ids));
    }

    @Override
    public List<LoginLogDTO> findAll(LoginLog loginLog) {
        Example<LoginLog> ex = Example.of(loginLog);
        return convertLoginLogListToDTOList(userLoginLogRepo.findAll(ex));
    }

    @Override
    public void delete(Integer id) throws BusinessException {
        LoginLog loginLog=userLoginLogRepo.findOne(id);
        if(loginLog==null){
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND,"id为"+id+"的日志不存在");
        }else{
            userLoginLogRepo.delete(id);
        }

    }

    @Override
    @Transactional
    public void delete(String ids) throws BusinessException {
        String[] id=ids.split(",");
        if(id.length<1){
            return;
        }
        for (int i = 0; i < id.length; i++) {
           delete(Integer.valueOf(id[i]));
        }

    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) throws BusinessException {
        for(int id:ids){
            delete(id);
        }
       /*     ids.forEach(id -> {
                try {
                    delete(id);
                } catch (BusinessException e) {
                    e.printStackTrace();

                }

            });*/

    }

    @Override
    public LoginLogDTO findById(Integer id) {

        return convertLoginLogToDTO(userLoginLogRepo.findOne(id));
    }

    @Override
    public LoginLogDTO save(LoginLog loginLog) {
        return convertLoginLogToDTO(userLoginLogRepo.save(loginLog));
    }

    @Override
    public void clean() {
        userLoginLogRepo.clean();
    }

    public LoginLogDTO convertLoginLogToDTO(LoginLog login){
        if(ObjectUtils.isEmpty(login)){
            return null;
        }
        LoginLogDTO a=new LoginLogDTO();
        BeanUtils.copyProperties(login,a);
        a.setUsername(login.getUser().getUsername());
        return a;
    }
    public  List<LoginLogDTO> convertLoginLogListToDTOList(List<LoginLog> list){
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        List<LoginLogDTO> dtoList=list.stream().map(elog->convertLoginLogToDTO(elog)).collect(Collectors.toList());
       // Page<LoginLogDTO> newPage=new PageImpl(dtoList,pageable,page.getTotalElements());
        return dtoList;
    }
}
