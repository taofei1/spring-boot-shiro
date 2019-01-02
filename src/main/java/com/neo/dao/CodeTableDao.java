package com.neo.dao;

import com.neo.entity.CodeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface CodeTableDao extends JpaRepository<CodeTable,Integer>,QueryByExampleExecutor<CodeTable> {

    List<CodeTable> findByCate(String cate);
    CodeTable findByCateAndAndCode(String cate,String code);
}
