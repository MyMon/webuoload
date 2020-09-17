package com.webupload.demo.common.consts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webupload.demo.common.consts.entity.DynamicCondition;
import com.webupload.demo.common.consts.entity.SoulTable;
import com.webupload.demo.common.consts.entity.SoulTableParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/12/21 21:30
 * @ClassName QueryWrapperConst
 ***/
public class QueryWrapperConst {
    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    /**
     * 查询构造
     */
    public static QueryWrapper queryConst(QueryWrapper queryWrapper, String paramJSONArray){
        if(paramJSONArray==null&&paramJSONArray.trim().length()==0){//参数json字符串为空，直接返回
            return queryWrapper;
        }
        List<DynamicCondition> dynamicList=new ArrayList<>();
        dynamicList= JSONArray.parseArray(paramJSONArray, DynamicCondition.class);
        if(queryWrapper!=null&&dynamicList!=null&&dynamicList.size()>0){
           for(DynamicCondition dynamic:dynamicList){
               /***
                * 属性操作
                * 	equal:等于
                * 	like:包含
                * 	between:范围
                * 	start:开头字符
                * 	end:结尾字符
                * 	unequal:不等于
                * 	empty:为空
                */
               if(dynamic.getConditionFieldVal()!=null&&dynamic.getConditionOptionVal()!=null){
                   String optionVal = dynamic.getConditionOptionVal();
                   String conditionFieldVal = dynamic.getConditionFieldVal();
                   conditionFieldVal=humpToLine(conditionFieldVal);
                   if(optionVal.equalsIgnoreCase("equal")){
                        queryWrapper.eq(conditionFieldVal,dynamic.getConditionValueVal());
                   }
                   if(optionVal.equalsIgnoreCase("between")){
                       queryWrapper.between(conditionFieldVal,dynamic.getConditionValueLeftVal(),dynamic.getConditionValueRightVal());
                   }
                   if(optionVal.equalsIgnoreCase("like")){
                       queryWrapper.like(conditionFieldVal,dynamic.getConditionValueVal());
                   }
                   if(optionVal.equalsIgnoreCase("start")){
                       queryWrapper.likeRight(conditionFieldVal,dynamic.getConditionValueLeftVal());
                   }
                   if(optionVal.equalsIgnoreCase("end")){
                       queryWrapper.likeLeft(conditionFieldVal,dynamic.getConditionValueRightVal());
                   }
                   if(optionVal.equalsIgnoreCase("unequal")){
                       queryWrapper.ne(conditionFieldVal,dynamic.getConditionValueVal());
                   }
               }
           }
        }
        return queryWrapper;
    }

    /**
     * SoulTable插件构建参数
     */
    public static QueryWrapper soulTableConst(QueryWrapper queryWrapper, SoulTableParam soulTableParam){
        if(soulTableParam==null){
            return queryWrapper;
        }
        String field = soulTableParam.getField();
        String order = soulTableParam.getOrder();
        /**
         * 排序构造
         */
        if(isNotEmpty(field)&&isNotEmpty(order)){
            field=humpToLine(field);
            if(order.equals("asc")){
                queryWrapper.orderByAsc(field);
            }else{
                queryWrapper.orderByDesc(field);
            }
        }

        String jsonfilterSos = soulTableParam.getFilterSos();
        System.out.println("jsonfilterSos:"+jsonfilterSos);
        if(!isNotEmpty(jsonfilterSos)){//为空，直接返回
            return queryWrapper;
        }
        List<SoulTable> filterSos = JSON.parseArray(jsonfilterSos, SoulTable.class);
        /***
         * 条件构造
         */
        if(filterSos!=null&&filterSos.size()>0){
            for(SoulTable soulTable:filterSos){
                if(soulTable.getMode().equals("condition")){//直接构造
                    queryWrapper=constSoulTable(queryWrapper,soulTable);
                }
                if(soulTable.getMode().equals("group")){//分组构造,暂时不考虑无限分组的情况
                    List<SoulTable> children = soulTable.getChildren();
                    if(children!=null&&children.size()>0){
                        for(SoulTable soulTablec:children){
                            System.out.println("soulTablec:"+soulTablec);
                            if(soulTablec.getMode().equals("condition")){//直接构造condition
                                queryWrapper=constSoulTable(queryWrapper,soulTablec);
                            }

                        }
                    }
                }
            }
        }
        return queryWrapper;
    }


    /***
     * 条件构造
     */
    public static QueryWrapper constSoulTable(QueryWrapper queryWrapper,SoulTable soulTable){
//        if(soulTable.getPrefix()=="and"){
//            queryWrapper.and(null);
//        }
//        if(soulTable.getPrefix()=="or"){
//            queryWrapper.or();
//        }
        String type=soulTable.getType();
        String field = soulTable.getField();//
        field=humpToLine(field);//转化下线形式
        String value = soulTable.getValue();
        if(type.equals("eq")){//等于 =
            queryWrapper.eq(field,value);
        }
        if(type.equals("ne")){//不等于 <>
            queryWrapper.ne(field,value);
        }
        if(type.equals("gt")){//大于
            queryWrapper.gt(field,value);
        }

        if(type.equals("ge")){//大于等于
            queryWrapper.ge(field,value);
        }

        if(type.equals("lt")){//小于
            queryWrapper.lt(field,value);
        }

        if(type.equals("le")){//小于等于
            queryWrapper.le(field,value);
        }

        if(type.equals("contain")){//包含
            queryWrapper.like(field,value);
        }

        if(type.equals("notContain")){//不包含
            queryWrapper.notLike(field,value);
        }
        if(type.equals("start")){//以***开始
            queryWrapper.likeRight(field,value);
        }

        if(type.equals("end")){//以***结束
            queryWrapper.likeLeft(field,value);
        }

        if(type.equals("in")){
            queryWrapper.in(field,new ArrayList<>(Arrays.asList(value.split(","))));
        }

        if(type.equals("null")){//为空
            queryWrapper.isNull(field);
        }

        if(type.equals("notNull")){//不为空
            queryWrapper.isNotNull(field);
        }
        return queryWrapper;
    }


    /***
     * 驼峰命名转化成下划线
     */
    public static String humpToLine(String str){
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /***
     * 字符判断是否为空
     * 不为空 true 否则false
     */
    public static boolean isNotEmpty(String str){
        return str!=null&&str.trim().length()>0;
    }
}
