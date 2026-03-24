package com.example_2.haidaodemo_v1.interceptors;

import com.example_2.haidaodemo_v1.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    public Result<String> handleRuntimeException(Exception e) {
        log.error("业务错误：{}",e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统错误：",e);
        return Result.error("系统错误，请稍候再试。");
    }
}
