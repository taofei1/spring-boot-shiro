package com.neo.service;

import com.neo.entity.CodeTable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CodeTableService {
    Page<CodeTable> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, CodeTable codeTable);
    Page<CodeTable> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<CodeTable> findAll();
    List<CodeTable> findAll(List<Integer> ids);
    List<CodeTable> findAll(CodeTable codeTable);
    void delete(Integer id);
    void delete(List<Integer> ids);
    CodeTable findById(Integer id);
    CodeTable save(CodeTable server);
}
