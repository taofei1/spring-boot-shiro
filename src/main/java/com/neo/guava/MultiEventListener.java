package com.neo.guava;

import com.google.common.eventbus.Subscribe;

/**
 * @Auther: 13965
 * @Date: 2018/11/6 14:39
 * @Description:
 * @Version: 1.0
 */
public class MultiEventListener {

    @Subscribe
    public void listen(OrderEvent event){
        System.out.println("receive msg: "+event.getMessage());
    }

    @Subscribe
    public void listen(String message){
        System.out.println("receive msg: "+message);
    }
}

