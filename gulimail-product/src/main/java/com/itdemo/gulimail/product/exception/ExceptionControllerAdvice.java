package com.itdemo.gulimail.product.exception;

import com.itdemo.common.exception.BizCodeEnum;
import com.itdemo.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice(basePackages = "com.itdemo.gulimail.product.app")
public class ExceptionControllerAdvice {

    //校验异常统一处理
    //处理异常类型
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R Handle(MethodArgumentNotValidException e){
            BindingResult result = e.getBindingResult();
            Map<String,String> map = new HashMap<>();
            result.getFieldErrors().forEach(( item )->{
                map.put(item.getField(),item.getDefaultMessage());
            });
            return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMessage()).put("data",map);
    }

    @ExceptionHandler(Exception.class)
    public R Handle(Exception e){
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMessage());
    }
}
