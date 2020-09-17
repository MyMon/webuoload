package com.webupload.demo.common.entity;

public enum ResultCode {
    SUCCESS(true,10000,"操作成功！"),
    //---系统错误返回码-----
    FAIL(false,10001,"操作失败"),
    UNAUTHORISE(false,10003,"权限不足"),
    VERIFICATIONFAILED(false,10004,"登录失效，请重新登录"),
    SERVER_ERROR(false,99999,"抱歉，系统繁忙，请稍后重试！"),

    //---用户操作返回码  2xxxx----
    LOGINERROR(false,20001,"用户名或密码错误"),
    DISABLELOGIN(false,20001,"您已经被管理员禁止登录"),
    NOTLOGIN(false,20002,"没有登录，请先登录"),

    //-----文件操作 3xxx-----
    FILEUPLOADED(true,30000,"文件上传成功"),
    FILENOTSUPPORT(false,30001,"不支持此类文件上传");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    ResultCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
