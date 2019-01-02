package com.neo.util;

import com.neo.entity.Email;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static String getFieldType(Class c,String field) {
        String simpleTypeName="";
        Method [] ms=c.getMethods();
        for(Method m:ms){
            if(m.getName().startsWith("get")&&m.getName().substring(3).equalsIgnoreCase(field)){
                simpleTypeName=m.getReturnType().getSimpleName();
            }
        }
        if(simpleTypeName=="") {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (f.getName().equalsIgnoreCase(field)) {
                    simpleTypeName = f.getType().getSimpleName();
                }
            }
        }


        return simpleTypeName;
    }
    public static void main(String[]  args) throws NoSuchMethodException {
        System.out.println(getFieldType(Email.class,"receiversId"));
    }
}
