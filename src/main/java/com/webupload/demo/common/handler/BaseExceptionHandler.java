package com.webupload.demo.common.handler;

import com.webupload.demo.common.CommonException;
import com.webupload.demo.common.entity.Result;
import com.webupload.demo.common.entity.ResultCode;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义的公共异常处理器
 *      1.声明异常处理器
 *      2.对异常统一处理
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if(e.getClass()== AuthenticationException.class){
           return new Result(ResultCode.VERIFICATIONFAILED);
        }else if(e.getClass() == CommonException.class) {
            //类型转型
            CommonException ce = (CommonException) e;
            Result result = new Result(ce.getResultCode());
            return result;
        }else if(e.getClass()== UnauthorizedException.class){
            return new Result(ResultCode.UNAUTHORISE);
        }else {
            Result result = new Result(ResultCode.SERVER_ERROR);
            return result;
        }
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public Result authenticationException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        return new Result(ResultCode.VERIFICATIONFAILED);
    }

}
