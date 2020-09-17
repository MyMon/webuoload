package com.webupload.demo.common.consts.entity;

import lombok.Data;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/12/29 12:18
 * @ClassName SoulTableParam
 ***/
@Data
public class SoulTableParam {
    /**
     * 条件集合
     **/
//    private List<SoulTable> filterSos;
    private String filterSos;

    /**
     * 属性名
     */
    private String field;
    /***
     * 排序方式
     * asc 或者 desc
     */
    private String order;

    /**
     * 页码
     */
    private Long page;
    /***
     * 分页大小
     */
    private Long size;
}
