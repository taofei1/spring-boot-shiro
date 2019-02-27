package com.neo.util;

import com.neo.enums.ErrorEnum;

public class Response {
    private String code;
    private Object data;

    public Response(String s,Object s1) {
        this.code=s;
        this.data=s1;
    }
    public Response(){}
    public static Response success(){
        return new Response("00", "²Ù×÷³É¹¦£¡");
    }
    public static Response fail(ErrorEnum e){
        return new Response(e.getCode(),e.getMessage());
    }
    public static Response fail(ErrorEnum e,String message){
        return new Response(e.getCode(),message);
    }

    public static Response error(String message) {
        return new Response("500", message);
    }
    public static Response success(Object data){
        return new Response("00",data);
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
