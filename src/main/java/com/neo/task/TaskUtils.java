package com.neo.task;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.quartz.SpringUtil;
import org.apache.log4j.Logger;
import org.thymeleaf.util.StringUtils;
public class TaskUtils {
    public final static Logger log = Logger.getLogger(TaskUtils.class);



    public static boolean invokMethod(SyncFilePlan syncFilePlan) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object object = null;
        Class clazz;
        boolean flag = true;
        if(syncFilePlan==null){
            return false;
        }

        TaskEntity taskEntity=syncFilePlan.getTaskEntity();
        if(taskEntity==null){
            return false;
        }
        if (syncFilePlan.getSpringId()!=null) {
            object = SpringUtil.getBean(syncFilePlan.getSpringId());
        } else if (!StringUtils.isEmpty(taskEntity.getClassName())) {
                clazz = Class.forName(taskEntity.getClassName());
                object = clazz.newInstance();

        }
        if (object == null) {
            flag = false;
            log.error("任务名称 = [" + syncFilePlan.getName() + "]---------------未启动成功，请检查是否配置正确！！！");
        }
        clazz = object.getClass();
        Method method ;
        String methodName="execute";
        if(!"".equals(taskEntity.getMethodName().trim())){
            methodName=taskEntity.getMethodName().trim();
        }
            method = clazz.getDeclaredMethod(methodName, new Class[] { String.class });

        if (method != null) {
            String param;
            if(!"".equals(taskEntity.getParam())){
                param=taskEntity.getParam();
            }else{
                param=syncFilePlan.getId().toString();
            }
            method.invoke(object, param);
        }
        if(flag){
            log.info("任务名称 = [" + syncFilePlan.getName() + "]----------启动成功");
            return true;

        }
        return flag;

    }

}