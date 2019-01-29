package com.neo.exception;

import com.neo.enums.ErrorEnum;

public class BusinessException  extends  Exception{
    private String code;
    private String message;
    public BusinessException(){
        super();
    }
    //指定错误传入错误emnu即可
    public BusinessException(ErrorEnum errorEnum){
        this.code=errorEnum.getCode();
        this.message=errorEnum.getMessage();
    }
    //通用错误传入指定信息 如：参数不合法
    public BusinessException(ErrorEnum errorEnum,String message){
        this.code=errorEnum.getCode();
        this.message=message;
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
