package com.neo.exception;

public enum ErrorEnum {
    DATA_NOT_FOUND("101","数据未找到"),
    PARAM_ERROR("102","参数有误"),
    UNKNOWN_ERROR("105","未知错误"),
    EXPORT_ERROR("202", "导出失败"),
    FILE_TO_LARGE("501", "文件过大"),
    DATABASE_OPER_ERROR("301", "数据库操作异常"),
    DUPLICATE_FILENAME("502", "文件名重复");
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
