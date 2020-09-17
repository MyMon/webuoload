package com.webupload.demo.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/11/25 0025 20:01
 * @ClassName PageResult
 ***/
@Data
//@ApiModel(value = "分页构造 PageResult",description = "分页构造")
public class PageResult<T> implements Serializable {
    //@ApiModelProperty(value = "数据总条数")
    private long total;
    //@ApiModelProperty(value = "行数据集合")
    private List<T> rows;

    public PageResult(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
}
