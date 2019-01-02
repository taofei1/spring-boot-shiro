package com.neo.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



import java.util.ArrayList;
import java.util.List;

public class PageableUtil {

    public static Pageable getPageable(int pageNum, int pageSize, String sortName, Integer type){
        if(sortName==null||"".equals(sortName)){
            return new PageRequest(pageNum,pageSize);
        }
        if(type==null){
            type=1;
        }
        List<String> nameList=new ArrayList();
        nameList.add(sortName);
        List<Integer> typeList=new ArrayList();
        typeList.add(type);
        return getPageable(pageNum,pageSize,nameList,typeList);
    }
    public static Pageable getMessagePageable(int pageNum, int pageSize, String sortName, Integer type){

        if(sortName==null||"".equals(sortName)||sortName.equals("auto")){
            sortName="createTime";
            type=1;
        }
        List<String> nameList=new ArrayList();
        nameList.add(sortName);
        List<Integer> typeList=new ArrayList();
        typeList.add(type);
        return getPageable(pageNum,pageSize,nameList,typeList);
    }
    public static Pageable getPageable(int pageNum, int pageSize, List<String> sortName, List<Integer> type){
        Pageable pageable;
        if(sortName==null||sortName.size()==0){
            pageable=new PageRequest(pageNum,pageSize);
        }else{

            List<Sort.Order> orders=new ArrayList<>();
            for(int i=0;i<sortName.size();i++){
                if(type.get(i)==0){
                    orders.add(new Sort.Order(Sort.Direction. ASC,sortName.get(i)));
                }else if(type.get(i)==1){
                    orders.add(new Sort.Order(Sort.Direction.DESC,sortName.get(i)));
                }else{
                    orders.add(new Sort.Order(sortName.get(i)));
                }
            }
            pageable=new PageRequest(pageNum,pageSize,new Sort(orders));
        }
        return pageable;
    }
}
