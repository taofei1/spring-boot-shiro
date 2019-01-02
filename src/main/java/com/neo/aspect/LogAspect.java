package com.neo.aspect;

import java.lang.reflect.Method;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.annotation.Log;
import com.neo.entity.Dept;
import com.neo.entity.UserInfo;
import com.neo.enums.OperateStatus;
import com.neo.factory.AsyncTaskFactory;
import com.neo.pojo.OperLog;
import com.neo.thread.ExecutorServiceHelper;
import com.neo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Aspect
@Component
@Slf4j
public class LogAspect
{

    // 配置织入点
    @Pointcut("@annotation(com.neo.annotation.Log)")
    public void logPointCut()
    {
    }

    /**
     * 返回通知 用于拦截操作
     * 返回之后调用
     * @param joinPoint 切点
     */
   // @AfterReturning("@annotation(com.neo.annotation.Log)")
    @AfterReturning(pointcut = "logPointCut()")
    public void doBefore(JoinPoint joinPoint)
    {
        handleLog(joinPoint, null);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e)
    {
        try
        {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);

            if (controllerLog == null)
            {
                return;
            }

            // 获取当前的用户
            UserInfo currentUser= ShiroUtil.getSysUser();

            // *========数据库日志=========*//
            OperLog operLog = new OperLog();
            operLog.setStatus(OperateStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IPUtils.getCurrentIp();
            operLog.setOperIp(ip);

            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            if (currentUser != null)
            {
                operLog.setOperName(currentUser.getUsername());
                if (StringUtils.isNotNull(currentUser.getDeptList())
                        && StringUtils.isNotEmpty(currentUser.getDeptList()))
                {
                    StringBuffer deptName=new StringBuffer("");
                    for(Dept dept:currentUser.getDeptList()){
                        deptName.append(dept.getName()).append(",");
                    }
                    if(!"".equals(deptName.toString())){
                        operLog.setDeptName(deptName.substring(0,deptName.length()-1));
                    }

                }
            }

            if (e != null)
            {
                operLog.setStatus(OperateStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 处理设置注解上的参数
            getControllerMethodDescription(controllerLog, operLog);
            // 保存数据库
            ExecutorServiceHelper.execute(AsyncTaskFactory.recordOper(operLog));
        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("后置通知异常，异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param
     * @return 方法描述
     * @throws Exception
     */
    public void getControllerMethodDescription(Log log, OperLog operLog) throws Exception
    {
        // 设置action动作
        operLog.setOperType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData())
        {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(operLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(OperLog operLog) throws Exception {
        String params;
        Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
       // params= RequestUtil.getBodyMsg(ServletUtils.getRequest());

        ObjectMapper om = new ObjectMapper();
        params = om.writeValueAsString(map);

        operLog.setOperParam(StringUtils.substring(params, 0, 255));
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null)
        {
            return method.getAnnotation(Log.class);
        }
        return null;
    }
}