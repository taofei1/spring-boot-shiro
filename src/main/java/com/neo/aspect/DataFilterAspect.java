package com.neo.aspect;

import com.neo.annotation.DataFilter;
import com.neo.entity.UserInfo;
import com.neo.pojo.BaseEntity;
import com.neo.pojo.OperLog;
import com.neo.util.GenUtils;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;

@Slf4j
@Aspect
@Component
public class DataFilterAspect {
    private static final String PARAMS = "filter";
    private static final String TABLEUSERINFO = "sys_user_info";
    private static String table;
    private static Method targetMethod;
    private static String userInfoColumn;
    private static String tableColumn;

    @Pointcut("@annotation(com.neo.annotation.DataFilter)")
    public void pointcut() {
    }

    ;

    @Before("pointcut()")
    public void before(JoinPoint jp) {
        handler(jp);
    }

    private void handler(JoinPoint jp) {
        //没有注解或没有添加表名直接返回
        DataFilter annotation = getAnnotationLog(jp);
        if (annotation == null) {
            return;
        }

        table = annotation.tableAlias();
        userInfoColumn = annotation.userInfoColumn();
        tableColumn = annotation.tableColumn();
        if (StringUtils.isEmpty(table)) {
            String name = targetMethod.getParameterTypes()[annotation.argLocation()].getName();
            log.info(name);
            table = GenUtils.fieldToColumn(name);

        }
        UserInfo userInfo = ShiroUtil.getSysUser();
        if (!userInfo.getUsername().equals("admin")) {
            filterData(jp, userInfo, annotation.argLocation());
        }
    }

    private void filterData(JoinPoint jp, UserInfo userInfo, int argsLoc) {
        BaseEntity baseEntity = (BaseEntity) jp.getArgs()[argsLoc];
        String sql = table + "." + tableColumn + "=" + "(select " + userInfoColumn + " from " + TABLEUSERINFO +
                " where uid='" + userInfo.getUid() + "')";
        log.info(sql);
        HashMap map = new HashMap();
        map.put(PARAMS, sql);
        baseEntity.setParams(map);
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private DataFilter getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = targetMethod = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(DataFilter.class);
        }
        return null;
    }

}
