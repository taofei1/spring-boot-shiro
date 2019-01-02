package com.neo.guava;

/**
 * @Auther: 13965
 * @Date: 2018/11/6 14:37
 * @Description:
 * @Version: 1.0
 */
//Guava 发布-订阅模式中传递的事件,是一个普通的POJO类
public class OrderEvent {  //事件
    private String message;

    public OrderEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
