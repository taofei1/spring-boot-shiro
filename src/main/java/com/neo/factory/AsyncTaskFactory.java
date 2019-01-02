package com.neo.factory;

import com.neo.exception.BusinessException;
import com.neo.pojo.OperLog;
import com.neo.quartz.SpringUtil;
import com.neo.service.OperLogService;
import com.neo.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * 异步任务产生工厂，都是耗时操作
 */
@Slf4j
public class AsyncTaskFactory {
    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final OperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作耗时
                operLog.setOperLocation(IPUtils.getLocationByIP(operLog.getOperIp()));
                try {
                    SpringUtil.getBean(OperLogService.class).insertOperlog(operLog);
                } catch (BusinessException e) {
                    log.error(e.getMessage(),e);

                }
            }
        };
    }

}
