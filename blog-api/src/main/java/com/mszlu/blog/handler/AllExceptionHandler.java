package com.mszlu.blog.handler;

import com.mszlu.blog.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author xiaokaixin
 * @Date 2021/8/15 14:18
 * @Version 1.0
 */
//对加了@Controller注解的方法进行拦截处理
@ControllerAdvice
public class AllExceptionHandler {

    //进行异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
