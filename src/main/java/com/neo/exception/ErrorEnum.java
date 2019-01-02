package com.neo.exception;

public enum ErrorEnum {
    DATA_NOT_FOUND("101","数据未找到"),
    PARAM_ERROR("102","参数有误"),
    UNKNOWN_ERROR("105","未知错误");
    private String code;
    private String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
