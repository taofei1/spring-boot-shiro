package com.neo.enums;

public enum SignType {
    REGISTER("注册"), LOGIN("登录"), LOGOUT("登出");
    private String type;

    SignType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
