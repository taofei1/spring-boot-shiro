package com.neo.exception;

import com.neo.enums.ErrorEnum;
import com.neo.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * ȫ���쳣������
 */
@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Response businessExceptionHandler(BusinessException e){
        log.error("ҵ���쳣��",e);
        return new Response(e.getCode(),e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public Response businessExceptionHandler(Exception e){
        log.error(e.getMessage(),e);
        return new Response(ErrorEnum.UNKNOWN_ERROR.getCode(),e.getMessage());
    }


}
