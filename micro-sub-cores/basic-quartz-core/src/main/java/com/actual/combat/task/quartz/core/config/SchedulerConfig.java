package com.actual.combat.task.quartz.core.config;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.annotation.Resource;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author yan
 * @DateTime 2024/6/23 14:39:51
 * @Description
 */
@Configuration
public class SchedulerConfig {

    //@Autowired
    //private JobFactory jobFactory;
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    //@Bean //使用quartz.properties需开启注释
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ClassPathResource location = new ClassPathResource("quartz.properties");
        propertiesFactoryBean.setLocation(location);
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


    public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

    public static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Resource
    private DataSource dataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        // 覆盖已存在的任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setDataSource(dataSource);
        // 延迟启动
        schedulerFactoryBean.setStartupDelay(1);
        //schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler() {
        JobFactory jobFactory = SpringUtil.getBean(JobFactory.class);
        Scheduler scheduler = schedulerFactoryBean(jobFactory).getScheduler();
        return scheduler;
    }

    //{
    //    @Resource
    //    private DataSource dataSource;
    //
    //    /**
    //     * 调度器
    //     *
    //     * @return
    //     * @throws Exception
    //     */
    //    @Bean
    //    public Scheduler scheduler() throws Exception {
    //    Scheduler scheduler = schedulerFactoryBean().getScheduler();
    //    return scheduler;
    //}
    //
    //    /**
    //     * Scheduler工厂类
    //     *
    //     * @return
    //     * @throws IOException
    //     */
    //    @Bean
    //    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    //    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    //    factory.setSchedulerName("Cluster_Scheduler");
    //    factory.setDataSource(dataSource);
    //    factory.setApplicationContextSchedulerContextKey("applicationContext");
    //    factory.setTaskExecutor(schedulerThreadPool());
    //    //factory.setQuartzProperties(quartzProperties());
    //    factory.setStartupDelay(10);// 延迟10s执行
    //    return factory;
    //}
    //
    //    /**
    //     * 配置Schedule线程池
    //     *
    //     * @return
    //     */
    //    @Bean
    //    public Executor schedulerThreadPool() {
    //    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //    executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
    //    executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
    //    executor.setQueueCapacity(Runtime.getRuntime().availableProcessors());
    //    return executor;
    //}    }


}

