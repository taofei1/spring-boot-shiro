package com.neo.enums;

public enum UserStatus {
    NORMAL(1, "正常"), DISABLE(2, "不可用");
    private Integer code;
    private String message;

    UserStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
