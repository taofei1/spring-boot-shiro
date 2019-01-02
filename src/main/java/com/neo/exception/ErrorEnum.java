package com.neo.exception;

public enum ErrorEnum {
    DATA_NOT_FOUND("101","����δ�ҵ�"),
    PARAM_ERROR("102","��������"),
    UNKNOWN_ERROR("105","δ֪����");
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
