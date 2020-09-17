package com.webupload.demo.common.base;


import com.webupload.demo.common.entity.PageResult;
import com.webupload.demo.common.entity.Result;
import com.webupload.demo.common.entity.ResultCode;

import java.util.List;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/12/20 13:26
 * @ClassName BaseController
 ***/
public class BaseController {
    /**
     * redirect跳转
     * @param url 目标url
     */
    protected String redirect(String url) {
        return new StringBuilder("redirect:").append(url).toString();
    }
    /**
     * @Description //TODO 返回成功并且有提示信息,不带返回值
     * @Author haijun
     * @Computer Administrator
     * @Date  17:34  2018/9/29/029
     * @Param [msg] 提示消息
     * @return java.lang.Object
     **/
    public Result renderSuccess(String msg){
        return new Result(ResultCode.SUCCESS,msg);
    }

    public Result renderSuccess(){
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * @Description //TODO 返回成功并且有提示信息,不带返回值
     * @Author haijun
     **/
    public Result renderError(){
        return new Result(ResultCode.FAIL);
    }

    public Result renderError(String msg){
        return new Result(ResultCode.FAIL,msg);
    }
    public Result renderError(ResultCode resultCode){
        return new Result(resultCode);
    }

    /**
     * 普通数据返回
     * @author haijun
     * @Description  //TODO
     * @Date 13:31 2019/12/20
     * @Param [data]
     * @return com.carpark.common.entity.Result
     **/
    public Result renderDataSuccess(Object data){
        return new Result(ResultCode.SUCCESS,data);
    }
    public Result renderDataSuccess(Object data,String msg){
        return new Result(ResultCode.SUCCESS,data,msg);
    }
    /***
     * 带分页查询返回
     * @param total
     * @param records
     * @return
     */
    public Result renderDataPageSuccess(long total, List records){
        return new Result(ResultCode.SUCCESS,new PageResult(total,records));
    }


}
