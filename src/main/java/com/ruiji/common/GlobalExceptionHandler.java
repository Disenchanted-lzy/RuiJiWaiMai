package com.ruiji.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        String msg;
        if(ex.getMessage().contains("Duplicate entry")){
            String[] a = ex.getMessage().split(" ");
            msg = a[2] + "已存在";
        }
        else
        {
            msg = "未知错误";
        }
        return R.error(msg);
    }

    @ExceptionHandler(CustomException.class)
    public R<String> ExceptionHandler(CustomException ex){

        return R.error(ex.getMessage());
    }
}
