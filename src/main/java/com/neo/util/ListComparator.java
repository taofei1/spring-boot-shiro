package com.neo.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

public class ListComparator implements Comparator {
    /***
     * 是否转化为Int之后再比较
     */
    private boolean isConvertInteger;
    /***
     * 对哪个列进行排序
     */
    private String comparedProperty;

    public ListComparator(boolean isConvertInteger, String comparedProperty) {
        super();
        this.isConvertInteger = isConvertInteger;
        this.comparedProperty = comparedProperty;
    }

    public int compare(Object o1, Object o2) {
        if (null != o1 && null != o2) {
            try {
                Object obj1 = ReflectUtil.getObjectValue(o1, comparedProperty);
                Object obj2 = ReflectUtil.getObjectValue(o2, comparedProperty);
                if (isConvertInteger) {
                    int num1;
                    int num2;
                    if (obj1 instanceof Integer) {
                        num1 = (Integer) obj1;
                        num2 = (Integer) obj2;
                    } else {
                        num1 = Integer.parseInt(obj1.toString());
                        num2 = Integer.parseInt(obj2.toString());
                    }
                    if (num1 > num2) {
                        return 1;
                    } else if (num1 < num2) {
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    return obj1.toString().compareTo(obj2.toString());
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return 0/*等于*/;
    }
}
