package com.actual_combat.aop.config.thread_pool;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2025/4/23 00:10:09
 * @Description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class GlobalThreadPoolTaskExecutor extends AbstractThreadPoolTaskExecutor {
}
