package com.actual.combat.listener;

import com.actual.combat.aop.abs.bean.AbsBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @Author yan
 * @Date 2025/3/21 10:06:21
 * @Description
 */
public class StartedEventListener implements ApplicationListener<ApplicationStartedEvent>, AbsBean {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        log().info("onApplicationEvent:[{}]","应用启动完成，通知监听器执行缓存预加载操作");
    }
}
