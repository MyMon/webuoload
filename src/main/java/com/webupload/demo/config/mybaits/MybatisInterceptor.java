package com.webupload.demo.config.mybaits;

import com.webupload.demo.common.util.OConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

/**
 * @Description TODO
 * @Author haijun
 * @Date 2019/12/24 0:14
 * @ClassName MybatisInterceptor
 ***/
@Slf4j
@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        log.debug("------sqlId------" + sqlId);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        log.debug("------sqlCommandType------" + sqlCommandType);

        if (parameter == null) {
            return invocation.proceed();
        }

        if (SqlCommandType.INSERT == sqlCommandType) {
            Field[] fields = OConvertUtils.getAllFields(parameter);
            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    // 注入创建时间createTime
                    if ("createTime".equals(field.getName())) {
                        field.setAccessible(true);
                        Object local_createTime = field.get(parameter);
                        field.setAccessible(false);
                        if (local_createTime == null || local_createTime.equals("")) {
                            field.setAccessible(true);
                            field.set(parameter, new Date());
                            field.setAccessible(false);
                        }
                    }
                   //逻辑删除注入(正常)
                    if("delFlag".equals(field.getName())){
                        field.setAccessible(true);
                        Object local_delFlag = field.get(parameter);
                        field.setAccessible(false);
                        if (local_delFlag == null || local_delFlag.equals("")) {
                            field.setAccessible(true);
                            field.set(parameter, 1);
                            field.setAccessible(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            Field[] fields = null;
            if (parameter instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> p = (MapperMethod.ParamMap<?>) parameter;
                if (p.containsKey("et")) {
                    parameter = p.get("et");
                } else {
                    parameter = p.get("param1");
                }
                fields = OConvertUtils.getAllFields(parameter);
            } else {
                fields = OConvertUtils.getAllFields(parameter);
            }

            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    if ("updateTime".equals(field.getName())) {
                        field.setAccessible(true);
                        Object local_updateDate = field.get(parameter);
                        field.setAccessible(false);
                        if (local_updateDate == null || local_updateDate.equals("")) {
                            field.setAccessible(true);
                            field.set(parameter, new Date());
                            field.setAccessible(false);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
