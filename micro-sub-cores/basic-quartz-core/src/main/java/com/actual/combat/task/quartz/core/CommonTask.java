package com.actual.combat.task.quartz.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("task")
@Slf4j
public class CommonTask {
    public void multipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        log.info("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i);
    }

    public void params(String params) {
        log.info("执行有参方法：{}", params);
    }

    public void noParams() {
        log.info("执行无参方法");
    }
}
