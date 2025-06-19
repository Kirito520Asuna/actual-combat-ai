# ***Quartz 分布式调度方案***

## 1.项目结构

```
quartz-module
└─src
    └─main
       ├─java
       │  └─com
       │      └─yan
       │          └─task
       │              └─quartz
       │                  ├─config
       │                  ├─dstributed
       │                  ├─enums
       │                  └─job
       └─resources
           └─db
```



## 2.pom

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.9</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.minimalism</groupId>
    <artifactId>quartz-module</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>quartz-module</name>
    <description>quartz-module</description>
    <url/>
    <licenses>
        <license/>
    </licenses>
    <developers>
        <developer/>
    </developers>
    <scm>
        <connection/>
        <developerConnection/>
        <tag/>
        <url/>
    </scm>
    <properties>
        <java.version>8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.18</version>
        </dependency>
<!--        <dependency>-->
<!--            <groupId>com.minimalism</groupId>-->
<!--            <artifactId>framework-redis</artifactId>-->
<!--            <version>0.0.1-SNAPSHOT</version>-->
<!--        </dependency>-->

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>

        <!-- mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>

        <!-- orm Mybatis-Plus 和 JPA 二选一-->
<!--        <dependency>-->
<!--            <groupId>org.springframework.boot</groupId>-->
<!--            <artifactId>spring-boot-starter-data-jpa</artifactId>-->
<!--        </dependency>-->

        <!--Mybatis-Plus-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.3</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 3.具体实现

### 3.1.quartz.sql

```sql
create database quartz;
use quartz;

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);
```

### 3.2 配置

#### 3.2.1 application.yml

```yml
spring:
  task:
  application:
    name: quartz-module
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/quartz?useSSL=false&serverTimezone=UTC
    username: username
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    max-active: 1000
    max-idle: 20
    min-idle: 5
    initial-size: 10

# 是否使用properties作为数据存储(以下配置与quartz.properties二选一即可)
org:
  quartz:
    jobStore:
      useProperties: false
      # 数据库中表的命名前缀
      tablePrefix: QRTZ_
      # 是否是一个集群，是不是分布式的任务
      isClustered: true
      # 集群检查周期，单位为毫秒，可以自定义缩短时间。当某一个节点宕机的时候，其他节点等待多久后开始执行任务
      clusterCheckinInterval: 5000
      # 单位为毫秒，集群中的节点退出后，再次检查进入的时间间隔
      misfireThreshold: 60000
      # 事务隔离级别
      txIsolationLevelReadCommitted: true
      # 存储的事务管理类型
      # class: org:quartz:impl.jdbcjobstore.JobStoreTX
      class: org:springframework.scheduling.quartz.LocalDataSourceJobStore
      # 使用的Delegate类型
      driverDelegateClass: org:quartz:impl.jdbcjobstore.StdJDBCDelegate
    # dataSource: quartzDataSource
    # 集群的命名，一个集群要有相同的命名
    scheduler:
      instanceName: ClusterQuartz
      # 节点的命名，可以自定义。AUTO代表自动生成
      instanceId: AUTO
      # rmi远程协议是否发布
      rmi.export: false
      # rmi远程协议代理是否创建
      rmi.proxy: false
      # 是否使用用户控制的事务环境触发执行任务
      wrapJobExecutionInUserTransaction: false
```

#### 3.2.2 quartz.properties

```properties
# 是否使用properties作为数据存储
org.quartz.jobStore.useProperties=false
# 数据库中表的命名前缀
org.quartz.jobStore.tablePrefix=QRTZ_
# 是否是一个集群，是不是分布式的任务
org.quartz.jobStore.isClustered=true
# 集群检查周期，单位为毫秒，可以自定义缩短时间。当某一个节点宕机的时候，其他节点等待多久后开始执行任务
org.quartz.jobStore.clusterCheckinInterval=5000
# 单位为毫秒，集群中的节点退出后，再次检查进入的时间间隔
org.quartz.jobStore.misfireThreshold=60000
# 事务隔离级别
org.quartz.jobStore.txIsolationLevelReadCommitted=true
# 存储的事务管理类型
#org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.class = org.springframework.scheduling.quartz.LocalDataSourceJobStore
# 使用的Delegate类型
org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#org.quartz.jobStore.dataSource: quartzDataSource
# 集群的命名，一个集群要有相同的命名
org.quartz.scheduler.instanceName=ClusterQuartz
# 节点的命名，可以自定义。AUTO代表自动生成
org.quartz.scheduler.instanceId=AUTO
# rmi远程协议是否发布
org.quartz.scheduler.rmi.export=false
# rmi远程协议代理是否创建
org.quartz.scheduler.rmi.proxy=false
# 是否使用用户控制的事务环境触发执行任务
org.quartz.scheduler.wrapJobExecutionInUserTransaction=false
```

### 3.3  代码实现

#### 3.3.1 枚举

***com.minimalism.task.quartz.enums***

##### ***CronTemplate***

```java
package com.minimalism.task.quartz.enums;

import cn.hutool.core.date.DatePattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @Author yan
 * @DateTime 2024/6/20 22:52:03
 * @Description
 */
@Getter
@AllArgsConstructor
public enum CronTemplate {
    MINUTE("0 0/%s * * * ?", TimeUnit.MINUTES, DatePattern.UTC_SIMPLE_PATTERN.replace(":ss", "")),
    HOUR("0 0 0/%s * * ?", TimeUnit.HOURS, DatePattern.UTC_SIMPLE_PATTERN.replace(":mm:ss", "")),
    CLOCK("0 0 %s * * ?", null, DatePattern.UTC_SIMPLE_PATTERN.replace(":mm:ss", ""));
    private String cronTemplate;
    private TimeUnit timeUnit;
    private String datePattern;
}
```

##### ***QuartzName***

```java
package com.minimalism.task.quartz.enums;


/**
 * @author Mysteriousman
 */
public enum QuartzName {
    /**
     * 每分钟
     */
    MINUTE_1,
    /**
     * 每5分钟
     */
    MINUTE_5,
    /**
     * 每10分钟
     */
    MINUTE_10,
    /**
     * 每15分钟
     */
    MINUTE_15,
    /**
     * 每30分钟
     */
    MINUTE_30,
    /**
     * 每1小时
     */
    HOUR_1,
    /**
     * 每2小时
     */
    HOUR_2,
    /**
     * 每3小时
     */
    HOUR_3,
    /**
     * 凌晨0点
     */
    CLOCK_0,
    /**
     * 凌晨2点
     */
    CLOCK_2,
    /**
     * 上午8点
     */
    CLOCK_8,
    /**
     * 上午9点
     */
    CLOCK_9,
    /**
     * 上午10点
     */
    CLOCK_10,
    /**
     * 上午12点
     */
    CLOCK_12,
    /**
     * 下午15点
     */
    CLOCK_15,
    /**
     * 下午17点
     */
    CLOCK_17,
    /**
     * 下午18点
     */
    CLOCK_18,
    /**
     * 晚上21点
     */
    CLOCK_21,
    /**
     * 晚上23点
     */
    CLOCK_23;
}
```

***QuartzGroup***

```java
package com.minimalism.task.quartz.enums;

/**
 * @author yan
 */
public enum QuartzGroup {
    /**
     * 当前默认组名
     */
    DEFAULT
}
```

#### 3.3.2 配置

***com.minimalism.task.quartz.config***

##### ***AutowiringSpringBeanJobFactory***

```java
package com.minimalism.task.quartz.config;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @Author yan
 * @DateTime 2024/6/23 15:45:32
 * @Description
 */
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AutowiringSpringBeanJobFactory.class);

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        //LOG.info("create job instance");
        beanFactory.autowireBean(job);
        return job;
    }
}
```

##### ***SchedulerConfig***

```java
package com.minimalism.task.quartz.config;

import cn.hutool.extra.spring.SpringUtil;
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

import javax.annotation.Resource;
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
}
```

##### ***QuartzObject***

```java
package com.minimalism.task.quartz.config;

import com.minimalism.task.quartz.enums.CronTemplate;
import com.minimalism.task.quartz.enums.QuartzGroup;
import com.minimalism.task.quartz.enums.QuartzName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.quartz.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class QuartzObject {
    private Class<Job> clockJobClass;
    private QuartzGroup quartzGroup;
    private QuartzName quartzName;
    private CronTemplate cronTemplate;
    private String value;
    private JobKey jobKey;
    private TriggerKey triggerKey;
    private CronScheduleBuilder scheduleBuilder;
    private JobDetail jobDetail;
    private Trigger trigger;
}
```

##### ***QuartzConfig***

```java
package com.minimalism.task.quartz.config;


import com.minimalism.task.quartz.enums.CronTemplate;
import com.minimalism.task.quartz.enums.QuartzGroup;
import com.minimalism.task.quartz.enums.QuartzName;
import com.minimalism.task.quartz.job.Clock0Job;
import com.minimalism.task.quartz.job.Minute1Job;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yao
 * @date 2024/5/28 17:52
 */
@Configuration
@Slf4j
public class QuartzConfig {
    public static Map<QuartzName, QuartzObject> QUARTZ_OBJECT_MAP = new HashMap<>();
    public static Map<Class<Job>, QuartzName> JOB_CLASS_QUARTZ_NAME_MAP = new HashMap<>();
    public static List<QuartzObject> quartzObjectList = new ArrayList<>();
    private static final Map<QuartzName, JobKey> JOB_KEY_MAP = new HashMap<>();
    private static final Map<QuartzName, CronScheduleBuilder> CRON_SCHEDULE_MAP = new HashMap<>();
    private static final Map<QuartzName, TriggerKey> TRIGGER_KEY_MAP = new EnumMap<>(QuartzName.class);
    private Map<QuartzName, JobDetail> JOB_DETAIL_MAP = new HashMap<>();

    static {
        Map<QuartzName, Class> quartzClassMap = new LinkedHashMap<>();
        quartzClassMap.put(QuartzName.MINUTE_1, Minute1Job.class);
        quartzClassMap.put(QuartzName.CLOCK_0, Clock0Job.class);

        log.info("~~~~~~~~~~~~~~~~~~QuartzConfig init~~~~~~~~~~~~~~~~~~");
        QuartzGroup quartzGroup = QuartzGroup.DEFAULT;
        String group = quartzGroup.name();
        Arrays.stream(QuartzName.values()).collect(Collectors.toList()).stream().forEach(quartzName -> {
            String name = quartzName.name();
            QuartzObject quartzObject = getQuartzObject(quartzName);

            JobKey jobKey = JobKey.jobKey(name, group);
            TriggerKey triggerKey = TriggerKey.triggerKey(name, group);

            //JOB_KEY_MAP.put(quartzName, jobKey);
            //TRIGGER_KEY_MAP.put(quartzName, triggerKey);
            String[] split = name.split("_");
            CronTemplate cronTemplate = CronTemplate.valueOf(split[0]);
            String value = split[1];
            String cronExpression = String.format(cronTemplate.getCronTemplate(), value);
            CronScheduleBuilder scheduleBuilder = buildCronScheduleBuilder(cronExpression);
            //CRON_SCHEDULE_MAP.put(quartzName, scheduleBuilder);


            quartzObject
                    .setQuartzGroup(quartzGroup)
                    .setQuartzName(quartzName)
                    .setCronTemplate(cronTemplate)
                    .setValue(value)
                    .setJobKey(jobKey)
                    .setTriggerKey(triggerKey)
                    .setScheduleBuilder(scheduleBuilder);
            QUARTZ_OBJECT_MAP.put(quartzName, quartzObject);
        });

        quartzClassMap.keySet().stream().forEach(quartzName -> {
            setClockJobClass(quartzName, quartzClassMap.get(quartzName));
        });

        List<QuartzObject> quartzObjects = QUARTZ_OBJECT_MAP.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        quartzObjectList.addAll(quartzObjects);

        quartzObjectList.stream().forEach(quartzObject -> {
            JOB_CLASS_QUARTZ_NAME_MAP.put(quartzObject.getClockJobClass(), quartzObject.getQuartzName());
        });
        log.info("~~~~~~~~~~~~~~~~~~QuartzConfig init end~~~~~~~~~~~~~~~~~~");
    }

    public static QuartzObject getQuartzObject(QuartzName quartzName) {
        QuartzObject quartzObject = QUARTZ_OBJECT_MAP.get(quartzName);
        if (quartzObject == null) {
            quartzObject = new QuartzObject();
            QUARTZ_OBJECT_MAP.put(quartzName, quartzObject);
        }
        return QUARTZ_OBJECT_MAP.get(quartzName);
    }

    public static QuartzObject setClockJobClass(QuartzName quartzName, Class clockJobClass) {
        QuartzObject quartzObject = getQuartzObject(quartzName);
        QUARTZ_OBJECT_MAP.put(quartzName, quartzObject.setClockJobClass(clockJobClass));
        return QUARTZ_OBJECT_MAP.get(quartzName);
    }

    public static CronScheduleBuilder buildCronScheduleBuilder(String cronExpression) {
        return CronScheduleBuilder.cronSchedule(cronExpression);
    }

    public JobDetail buildNewJobDetail(Class clockJob, JobKey jobKey) {
        return JobBuilder.newJob(clockJob).withIdentity(jobKey).storeDurably().build();
    }

    public Trigger buildNewTrigger(CronScheduleBuilder scheduleBuilder, JobKey jobKey, TriggerKey triggerKey) {
        return TriggerBuilder.newTrigger().forJob(jobKey).withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
    }

    public JobDetail buildNewJobDetail(Class clockJob, QuartzName quartzName) {
        QuartzObject quartzObject = getQuartzObject(quartzName);
        JobKey jobKey = quartzObject.getJobKey();
        JobDetail jobDetail = buildNewJobDetail(clockJob, jobKey);
        //JOB_DETAIL_MAP.put(quartzName, jobDetail);
        QUARTZ_OBJECT_MAP.put(quartzName, quartzObject.setJobDetail(jobDetail));
        return jobDetail;
    }

    public JobDetail buildNewJobDetail(QuartzName quartzName) {
        QuartzObject quartzObject = getQuartzObject(quartzName);
        Class clockJobClass = quartzObject.getClockJobClass();
        JobKey jobKey = quartzObject.getJobKey();
        JobDetail jobDetail = buildNewJobDetail(clockJobClass, jobKey);
        //JOB_DETAIL_MAP.put(quartzName, jobDetail);
        QUARTZ_OBJECT_MAP.put(quartzName, quartzObject.setJobDetail(jobDetail));
        return jobDetail;
    }

    public Trigger buildNewTrigger(QuartzName quartzName) {
        QuartzObject quartzObject = getQuartzObject(quartzName);
        CronScheduleBuilder build = quartzObject.getScheduleBuilder();
        JobKey jobKey = quartzObject.getJobKey();
        TriggerKey triggerKey = quartzObject.getTriggerKey();
        //CronScheduleBuilder build = CRON_SCHEDULE_MAP.get(quartzName);
        //JobKey jobKey = JOB_KEY_MAP.get(quartzName);
        //TriggerKey triggerKey = TRIGGER_KEY_MAP.get(quartzName);
        Trigger trigger = buildNewTrigger(build, jobKey, triggerKey);
        QUARTZ_OBJECT_MAP.put(quartzName, quartzObject.setTrigger(trigger));
        return trigger;
    }

    public Trigger buildNewTrigger(JobDetail jobDetail, QuartzName quartzName) {
        QuartzObject quartzObject = getQuartzObject(quartzName);

        CronScheduleBuilder build = quartzObject.getScheduleBuilder();
        //JobKey jobKey = quartzObject.getJobKey();
        TriggerKey triggerKey = quartzObject.getTriggerKey();

        //CronScheduleBuilder build = CRON_SCHEDULE_MAP.get(quartzName);
        //JobKey jobKey = JOB_KEY_MAP.get(quartzName);
        //TriggerKey triggerKey = TRIGGER_KEY_MAP.get(quartzName);
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(triggerKey).withSchedule(build).build();
    }

    @Bean
    public JobDetail clock0JobDetail() {
        return buildNewJobDetail(QuartzName.CLOCK_0);
    }

    @Bean
    public Trigger clock0Trigger() {
        return buildNewTrigger(QuartzName.CLOCK_0);
    }

    @Bean
    public JobDetail minute1JobDetail() {
        log.info("~~~~~~~~~~~~~~~~~~minute1JobDetail~~~~~~~~~~~~~~~~~~");
        return buildNewJobDetail(QuartzName.MINUTE_1);
    }

    @Bean
    public Trigger minute1Trigger() {
        log.info("~~~~~~~~~~~~~~~~~~minute1Trigger~~~~~~~~~~~~~~~~~~");
        return buildNewTrigger(QuartzName.MINUTE_1);
    }
}
```

##### ***JobStartupRunner***

```java
package com.minimalism.task.quartz.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author yan
 * @DateTime 2024/6/23 14:42:09
 * @Description
 */
@Component
@Slf4j
public class JobStartupRunner implements CommandLineRunner {
    @Resource
    SchedulerConfig schedulerConfig;

    @Override
    public void run(String... args) throws Exception {
        Scheduler scheduler;
        try {
            scheduler = schedulerConfig.scheduler();
            List<QuartzObject> quartzObjectList = QuartzConfig.quartzObjectList;
            quartzObjectList.stream().forEach(quartzObject -> {
                TriggerKey triggerKey = quartzObject.getTriggerKey();
                JobKey jobKey = quartzObject.getJobKey();
                Trigger trigger = quartzObject.getTrigger();
                JobDetail jobDetail = quartzObject.getJobDetail();
                try {
                    CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    if (null == cronTrigger && jobDetail != null && trigger != null) {
                        scheduler.scheduleJob(jobDetail, trigger);
                        log.info("==> Quartz 创建了job: {} <==", jobDetail.getKey());
                    } else {
                        if (jobDetail != null) {
                            log.info("==> {} job已存在 <==", jobDetail.getKey());
                        } else {
                            log.info("==> {} job未初始化 <==", jobKey);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            scheduler.start();
        } catch (Exception e) {
            throw e;
        }
    }
}
```

#### 3.3.3 禁止并发执行

##### ***DistributedJob***

```java
package com.minimalism.task.quartz.dstributed;

import cn.hutool.core.date.DatePattern;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author yan
 * @DateTime 2024/6/23 14:56:39
 * @Description 分布式 Job
 */
// 持久化
@PersistJobDataAfterExecution
// 禁止并发执行
@DisallowConcurrentExecution
public class DistributedJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Logger log = LoggerFactory.getLogger(this.getClass());
        String taskName = context.getJobDetail().getJobDataMap().getString("name");
        log.info("===> Quartz job, time:{" + DateTimeFormatter.ofPattern(DatePattern
                .NORM_DATETIME_PATTERN).format(LocalDateTime.now()) + "} ,name:{" + taskName + "} <===");
    }
}
```

##### ***job 事例***

```java
package com.minimalism.task.quartz.job;

import com.minimalism.task.quartz.dstributed.DistributedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Yao
 * @date 2024/3/4 17:25
 */

@Slf4j
@Component
public class Clock0Job extends DistributedJob {


    //@Override
    //public void executeDistributed(JobExecutionContext var1) throws JobExecutionException {
    //    log.info("~~~~~~~~~~~~~~~~~~~~~~~~~Clock0Job~~~~~~~~~~~~~~~~~~~~~~~~~");
    //}

    //@Override
    //public void execute(JobExecutionContext context) throws JobExecutionException {
    //    log.info("~~~~~~~~~~~~~~~~~~~~~~~~~Clock0Job~~~~~~~~~~~~~~~~~~~~~~~~~");
    //}
}
```

```java
package com.minimalism.task.quartz.job;


import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.task.quartz.dstributed.DistributedJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class Minute1Job extends DistributedJob {

    //@Override
    //public void executeDistributed(JobExecutionContext var1) throws JobExecutionException {
    //    log.info("~~~~~~~~~~~~~~~~~~~~~~~~~Minute1Job~~~~~~~~~~~~~~~~~~~~~~~~~");
    //}

    //@Override
    //public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    //    log.info("~~~~~~~~~~~~~~~~~~~~~~~~~Minute1Job~~~~~~~~~~~~~~~~~~~~~~~~~");
    //}
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ThreadPoolTaskExecutor executor = SpringUtil.getBean(ThreadPoolTaskExecutor.class);
        Minute1Job.super.executeInternal(context);
        log.info("~~~~~~~~~~~~~~~~~~~~~~~~~{}~~~~~~~~~~~~~~~~~~~~~~~~~", this.getClass().getSimpleName());
        //CustThreadPoolTaskExecutor runAsync = new CustThreadPoolTaskExecutor().setCustThreadNamePrefix("runAsync");
        CompletableFuture.runAsync(() -> {
            log.info("===> {} <===", "runAsync");
        }, executor);
    }

}
```

#### ***3.3.4 启动类***

```java
package com.minimalism;

import com.actual.combat.BasicQuartzApplication;
import com.actual.combat.QuartzApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuartzModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicQuartzApplication.class, args);
    }

}
```