package com.neo.service.impl;

import com.github.pagehelper.PageHelper;
import com.neo.exception.BusinessException;
import com.neo.enums.ErrorEnum;
import com.neo.mapper.OperLogMapper;
import com.neo.pojo.OperLog;
import com.neo.service.OperLogService;
import com.neo.util.GenUtils;
import com.neo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OperLogServiceImpl implements OperLogService{
    @Autowired
    private OperLogMapper operLogMapper;
    @Override
    public void insertOperlog(OperLog operLog) throws BusinessException {
        if(!StringUtils.isNull(operLog)){
            operLogMapper.insertOperlog(operLog);
        }else{
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
    }

    @Override
    public void deleteOperLogByIds(String ids) throws BusinessException {
        if(ids==null){
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        String[] id=ids.split(",");
        if(id.length==1){
            operLogMapper.deleteOne(Long.valueOf(id[0]));
        }else{
            operLogMapper.deleteOperLogByIds(id);
        }
    }


    @Override
    public List<OperLog> selectList(Integer pageNum,Integer pageSize,OperLog operLog) {
        String orderBy="";
        Map<String, Object> map=operLog.getParams();
        if(map.get("sort")!=null){
            orderBy+= GenUtils.fieldToColumn((String) map.get("sort"));
            if(map.get("order")!=null){
                orderBy+=" "+map.get("order");
            }
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        return operLogMapper.selectOperLogList(operLog);
    }

    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }

    @Override
    public OperLog getOne(Long id) {
        return operLogMapper.selectOperLogById(id);
    }
}
