package com.neo.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;



public class Dto2Entity {

    /**
     * 将dto和entity之间的属性互相转换,dto中属性一般为String等基本类型,
     * <p>
     * 但是entity中可能有复合主键等复杂类型,需要注意同名问题
     *
     * @param src
     * @param target
     */

    public static Object populate(Object src, Object target) {

        Method[] srcMethods = src.getClass().getMethods();

        Method[] targetMethods = target.getClass().getMethods();

        for (Method m : srcMethods) {

            String srcName = m.getName();
            Type srcType =m.getGenericReturnType();
            if (srcName.startsWith("get")) {

                try {

                    Object result = m.invoke(src);

                    for (Method mm : targetMethods) {
                        String parmType="";
                        String targetName = mm.getName();
                        if(mm.getParameters().length>0){
                            parmType=mm.getParameterTypes()[0].getTypeName();
                        }
                        if (targetName.startsWith("set")
                                && targetName.substring(3, targetName.length())

                                .equals(srcName.substring(3, srcName.length()))&&srcType.getTypeName().equals(parmType)) {

                            mm.invoke(target, result);

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        return target;

    }

    /**
     * dto集合和实体类集合间的互相属性映射
     *
     * @param src
     * @param target
     * @param targetClass
     * @return
     */

    @SuppressWarnings("unchecked")
    public static <S, T> List<T> populateList(List<S> src, List<T> target,
                                              Class<?> targetClass) {

        for (int i = 0; i < src.size(); i++) {

            try {

                Object object = targetClass.newInstance();

                target.add((T) object);

                populate(src.get(i), object);

            } catch (Exception e) {

                continue;// 某个方法反射异常

            }

        }

        return target;

    }
    public static Object copyObject(Object src,Object target){
        if(src==null){
            return null;
        }
         BeanUtils.copyProperties(src,target);
        return target;
    }

}