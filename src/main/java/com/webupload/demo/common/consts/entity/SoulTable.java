package com.webupload.demo.common.consts.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * TODO SoulTable参数构造
 * @Author haijun
 * @Date 2019/12/29 12:20
 * @ClassName SoulTable
 ***/
@Data
public class SoulTable {
    private boolean head;
    private String prefix;
    private String mode;
    private String field;
    private String value;
    private String type;
    private String groupId;

    private List<SoulTable> children;//子集合
}
