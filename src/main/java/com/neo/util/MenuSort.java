
package com.neo.util;

import com.neo.entity.RolePermission;

import java.util.Comparator;

public class MenuSort implements Comparator<RolePermission> {


    @Override
    public int compare(RolePermission o1, RolePermission o2) {
        if(o1==null||o1==null&&o2==null){
            return 1;
        }else if(o2==null){
            return -1;
        }else if(o1.getSort()<o2.getSort()){
            return -1;
        }else{
         return 1;
        }
    }
}

