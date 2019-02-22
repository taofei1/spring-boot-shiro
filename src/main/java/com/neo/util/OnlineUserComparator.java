package com.neo.util;

import com.neo.enums.OnlineStatus;
import com.neo.model.online.OnlineUser;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;

public class OnlineUserComparator implements Comparator<OnlineUser> {

    private String field;
    private String order;

    public OnlineUserComparator(String field, String order) {
        this.field = field;
        this.order = order;
    }

    @Override
    public int compare(OnlineUser o1, OnlineUser o2) {
        try {
            Field field = o1.getClass().getField(this.field);
            field.setAccessible(true);
            Object o = field.get(o1);
            Object n = field.get(o2);
            if (field.getType() == String.class) {
                String s1 = (String) o;
                String s2 = (String) n;
                return order.equalsIgnoreCase("desc") ? s2.compareTo(s1) : s1.compareTo(s2);
            } else if (field.getType() == Long.class) {
                Long l1 = (Long) o;
                Long l2 = (Long) n;
                return (int) (order.equalsIgnoreCase("desc") ? l2 - l1 : l1 - l2);
            } else if (field.getType() == Date.class) {
                Date d1 = (Date) o;
                Date d2 = (Date) n;
                return order.equalsIgnoreCase("desc") ? d2.compareTo(d1) : d1.compareTo(d2);

            } else if (field.getType() == OnlineStatus.class) {
                OnlineStatus os1 = (OnlineStatus) o;
                OnlineStatus os2 = (OnlineStatus) n;
                return order.equalsIgnoreCase("desc") ? os2.ordinal() - os1.ordinal() : os1.ordinal() - os2.ordinal();
            } else {
                return 0;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
