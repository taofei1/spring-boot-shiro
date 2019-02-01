package com.neo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageModel<T> {
    //当前页数
    private int pageNum;
    //每页行数
    private int pageSize;
    //总共的页数
    private int totalPages;
    //总共记录数
    private int totalNum;
    //排序字段
    private String sort;
    //升降序
    private String order;
    //搜索字段：搜索值
    private Map<String, String> search = new HashMap<>();
    private List<T> list;

}
