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
 * ������־��¼����
 *
 * @author ruoyi
 */
@Aspect
@Component
@Slf4j
public class LogAspect
{

    // ����֯���
    @Pointcut("@annotation(com.neo.annotation.Log)")
    public void logPointCut()
    {
    }

    /**
     * ����֪ͨ �������ز���
     * ����֮�����
     * @param joinPoint �е�
     */
   // @AfterReturning("@annotation(com.neo.annotation.Log)")
    @AfterReturning(pointcut = "logPointCut()")
    public void doBefore(JoinPoint joinPoint)
    {
        handleLog(joinPoint, null);
    }

    /**
     * �����쳣����
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
            // ���ע��
            Log controllerLog = getAnnotationLog(joinPoint);

            if (controllerLog == null)
            {
                return;
            }

            // ��ȡ��ǰ���û�
            UserInfo currentUser= ShiroUtil.getSysUser();

            // *========���ݿ���־=========*//
            OperLog operLog = new OperLog();
            operLog.setStatus(OperateStatus.SUCCESS.ordinal());
            // ����ĵ�ַ
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
            // ���÷�������
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // ��������ע���ϵĲ���
            getControllerMethodDescription(controllerLog, operLog);
            // �������ݿ�
            ExecutorServiceHelper.execute(AsyncTaskFactory.recordOper(operLog));
        }
        catch (Exception exp)
        {
            // ��¼�����쳣��־
            log.error("����֪ͨ�쳣���쳣��Ϣ:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * ��ȡע���жԷ�����������Ϣ ����Controller��ע��
     *
     * @param
     * @return ��������
     * @throws Exception
     */
    public void getControllerMethodDescription(Log log, OperLog operLog) throws Exception
    {
        // ����action����
        operLog.setOperType(log.businessType().ordinal());
        // ���ñ���
        operLog.setTitle(log.title());
        // ���ò��������
        operLog.setOperatorType(log.operatorType().ordinal());
        // �Ƿ���Ҫ����request��������ֵ
        if (log.isSaveRequestData())
        {
            // ��ȡ��������Ϣ�����뵽���ݿ��С�
            setRequestValue(operLog);
        }
    }

    /**
     * ��ȡ����Ĳ������ŵ�log��
     *
     * @param operLog ������־
     * @throws Exception �쳣
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
     * �Ƿ����ע�⣬������ھͻ�ȡ
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