package com.neo.model;

import java.io.Serializable;

/**
 * @Auther: 13965
 * @Date: 2018/9/20 17:33
 * @Description:
 * @Version: 1.0
 */

public class Car implements Serializable {
    public Car(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
