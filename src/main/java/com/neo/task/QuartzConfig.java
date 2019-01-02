package com.neo.task;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private JobFactory jobFactory;



    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        //schedulerFactoryBean.setStartupDelay(20);
        //用于quartz集群,加载quartz数据源配置
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        //注册触发器

        return schedulerFactoryBean;
    }

    private Properties quartzProperties() {
        Properties prop = new Properties();
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "100");
        return prop;
    }

    @Bean(value = "scheduler")
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }
}