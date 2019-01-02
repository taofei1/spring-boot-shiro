package com.neo.util;

import org.apache.shiro.crypto.hash.SimpleHash;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Util {
    public static void main(String[] args) {
        String uuid= UUID.randomUUID().toString().replace("-","");
        System.out.println(uuid);
        System.out.println(MD5Pwd("123456",uuid,2));
    }
    public static String  MD5Pwd(String sourcePwd,String salt,int hashIterations) {
        String hashAlgorithmName = "MD5";
       // String uuid = UUID.randomUUID().toString().replace("-", "");
      //  System.out.println(uuid);
     //   String salt = "wang" + uuid;
   //     int hashIterations = 2;

        SimpleHash obj = new SimpleHash(hashAlgorithmName, sourcePwd, salt, hashIterations);
        return obj.toString();
    }
    /**
     * 生成随即密码
     * @param pwd_len 生成的密码的总长度
     * @return  密码的字符串
     */
    public static String genRandomNum(int pwd_len){
        //35是因为数组是从0开始的，26个字母+10个数字
        final int  maxNum = 73;
        int i;  //生成的随机数
        int count = 0; //生成的密码的长度
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','_','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };

        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < pwd_len){
            //生成随机数，取绝对值，防止生成负数，

            i = Math.abs(r.nextInt(maxNum));  //生成的数最大为36-1

            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }

        return pwd.toString();
    }
    public final static boolean isNull(Object[] objs) {
        if (objs == null || objs.length == 0) return true;
        return false;
    }

    public final static boolean isNull1(Object obj) {
        if (obj == null || isNull(obj.toString())) {
            return true;

        }
        System.out.println(obj);
        return false;
    }

    public final static boolean isNull(Integer integer) {
        if (integer == null || integer == 0) return true;
        return false;
    }

    public final static boolean isNull(Collection collection) {
        if (collection == null || collection.size() == 0) return true;
        return false;
    }

    public final static boolean isNull(Map map) {
        if (map == null || map.size() == 0) return true;
        return false;
    }

    public final static boolean isNull(String str) {
        return str == null || "null".equals(str.toLowerCase());
    }

    public final static boolean isNull(Long longs) {
        if (longs == null || longs == 0) return true;
        return false;
    }

    public final static boolean isNotNull(Long longs) {
        return !isNull(longs);
    }

    public final static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public final static boolean isNotNull(Collection collection) {
        return !isNull(collection);
    }

    public final static boolean isNotNull(Map map) {
        return !isNull(map);
    }

    public final static boolean isNotNull(Integer integer) {
        return !isNull(integer);
    }

    public final static boolean isNotNull(Object[] objs) {
        return !isNull(objs);
    }

    public final static boolean isNotNull(Object obj) {
        return !isNull1(obj);
    }

    public static final boolean isNull(Object obj) {

        Field[] fields = obj.getClass().getDeclaredFields();
        //用于判断所有属性是否为空,如果参数为空则不查询
        boolean flag = true;
        for (Field field : fields) {
            //不检查 直接取值
            field.setAccessible(true);
            try {
                if (isNotNull(field.get(obj))) {
                    //不为空
                    flag = false;
                    //当有任何一个参数不为空的时候则跳出判断直接查询
                    break;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}
