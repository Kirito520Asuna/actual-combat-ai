package com.actual.combat.task.xxl_job.work;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @DateTime 2024/6/22 3:51:45
 * @Description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobTest {

    @XxlJob("xxlJobTest")
    public ReturnT<String> xxlJobTest(String date) {
        log.info("---------xxlJobTest定时任务执行成功--------");
        return ReturnT.SUCCESS;
    }

}
