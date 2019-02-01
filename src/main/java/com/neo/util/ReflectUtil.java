package com.neo.util;

import com.neo.entity.Email;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static String getFieldType(Class c, String field) {
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

    public static String getFieldByNameAndClass(String name, Class<?> clazz) {
        if (StringUtils.isEmpty(name) || StringUtils.isNull(clazz)) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field.getName();
            }
        }
        return null;
    }

    public static Object getObjectValue(Object o, String property) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String firstLetter = property.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + property.substring(1);

        Method m = o.getClass().getMethod(getter);
        Object value3 = m.invoke(o);

        return value3;

    }
    public static void main(String[]  args) throws NoSuchMethodException {
        System.out.println(getFieldType(Email.class,"receiversId"));
    }
}
