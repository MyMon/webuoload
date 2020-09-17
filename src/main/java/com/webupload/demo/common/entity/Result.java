package com.webupload.demo.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/11/25 0025 0:15
 * @ClassName Result
 ***/
@Data
@NoArgsConstructor
public class Result implements Serializable {
    //@ApiModelProperty(value = "操作标识，false失败，true成功")
    private boolean success;//是否成功
    //@ApiModelProperty(value = "数据总条数，状态码")
    private Integer code;// 返回码
    //@ApiModelProperty(value = "响应提示消息")
    private String message;//返回信息
    //@ApiModelProperty(value = "响应数据，可能是对象，也可能是数组对象，具体看业务需求")
    private Object data;// 返回数据

    public Result(ResultCode code) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
    }

    public Result(ResultCode code,String message) {
        this.success = code.success;
        this.code = code.code;
        this.message = message;
    }

    public Result(ResultCode code,Object data) {
        this.success = code.success;
        this.code = code.code;
        this.message = code.message;
        this.data = data;
    }

    public Result(Integer code,String message,boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public Result(ResultCode success, Object data, String msg) {
        this.data=data;
        this.code = success.code;
        this.message = msg;
        this.success = success.success;
    }


    public static Result SUCCESS(){
        return new Result(ResultCode.SUCCESS);
    }

    public static Result ERROR(){
        return new Result(ResultCode.SERVER_ERROR);
    }

    public static Result FAIL(){
        return new Result(ResultCode.FAIL);
    }

}
