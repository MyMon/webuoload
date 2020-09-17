package com.webupload.demo.common.entity;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/11/25 0025 20:03
 * @ClassName StatusCode
 ***/
//@ApiModel(value = "响应状态码")
public class StatusCode {
    //@ApiModelProperty(value = "20000，操作成功")
    public static final int OK=20000;//成功
    //@ApiModelProperty(value = "20001，操作失败")
    public static final int ERROR=20001;//失败
    //@ApiModelProperty(value = "20001，用户名或密码错误")
    public static final int LOGINERROR=20002;//用户名或密码错误
    //@ApiModelProperty(value = "20003，权限不足")
    public static final int ACCESSERROR=20003;//权限不足
    //@ApiModelProperty(value = "20004，远程调用失败")
    public static final int REMOTEERROR=20004;//远程调用失败
    //@ApiModelProperty(value = "20005，重复操作")
    public static final int REPERROR=20005;//重复操作

}
