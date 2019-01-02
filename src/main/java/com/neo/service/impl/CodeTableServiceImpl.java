package com.neo.service.impl;

import com.neo.dao.CodeTableDao;
import com.neo.entity.CodeTable;
import com.neo.service.CodeTableService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service("codeTableService")
public class CodeTableServiceImpl implements CodeTableService {
    @Autowired
    private CodeTableDao codeTableDao;
    @Override
    public Page<CodeTable> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, CodeTable codeTable) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("code", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("value", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("cateName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("cate", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CodeTable> ex = Example.of(codeTable, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        return codeTableDao.findAll(ex,pageable);
    }

    @Override
    public Page<CodeTable> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return codeTableDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));

    }

    @Override
    public List<CodeTable> findAll() {
        return codeTableDao.findAll();
    }

    @Override
    public List<CodeTable> findAll(List<Integer> ids) {
        return codeTableDao.findAll(ids);
    }

    @Override
    public List<CodeTable> findAll(CodeTable codeTable) {
        Example<CodeTable> ex = Example.of(codeTable);
        return codeTableDao.findAll(ex);
    }

    @Override
    public void delete(Integer id) {
        codeTableDao.delete(id);
    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public CodeTable findById(Integer id) {
        return codeTableDao.findOne(id);
    }

    @Override
    public CodeTable save(CodeTable codeTable) {
        return codeTableDao.save(codeTable);
    }

    /**
     * 以下两种方法仅供thymeleaf直接调用
     * @param cate 分类名
     * @return  列表
     */
    public List<CodeTable> findByCate(String cate){
        return codeTableDao.findByCate(cate);
    }
    public CodeTable findByCateAndCode(String cate,String code){
        return codeTableDao.findByCateAndAndCode(cate,code);
    }

}
