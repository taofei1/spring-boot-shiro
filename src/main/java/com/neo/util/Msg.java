package com.neo.util;


import com.neo.enums.ErrorEnum;

public class Msg<T> {
    private String code;
    private String msg;
    private T data;

    public Msg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Msg(ErrorEnum comm){
        this.code=comm.getCode();
        this.msg=comm.getMessage();
    }
    public Msg(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public Msg(){}
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public static Msg success(){
        return new Msg("00","请求成功！");
    }
    public Msg addData(T data){
        this.setData(data);
        return this;
    }
    public static  Msg fail(){
        return new Msg("01","请求失败！");
    }

}
