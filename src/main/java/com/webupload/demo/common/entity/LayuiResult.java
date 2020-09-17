package com.webupload.demo.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/****
 * @ClassName LayuiResult
 * @Description TODO layui格式的结果集
 * @Computer Administrator
 * @Auther haijun
 * @Date 2018/9/30/030 10:22
 * @Version 1.0
 ***/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayuiResult implements Serializable {
    /**
     * 状态码 默认是0
     */
    private Integer code;
    /**
     * 状态信息
     */
    private String msg;
    /**
     * 数据总条数
     */
    private Integer count;
    /**
     * 数据结果集
     */
    private Object data;


}
