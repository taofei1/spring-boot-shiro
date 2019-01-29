package com.neo.exception;

import com.neo.enums.ErrorEnum;
import com.neo.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Response businessExceptionHandler(BusinessException e){
        log.error("业务异常：",e);
        return new Response(e.getCode(),e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public Response businessExceptionHandler(Exception e){
        log.error(e.getMessage(),e);
        return new Response(ErrorEnum.UNKNOWN_ERROR.getCode(),e.getMessage());
    }


}
