package com.actual.combat.ai.langchain4j.tools;

import jakarta.annotation.Resource;

/**
 * @Author yan
 * @Date 2025/7/3 17:59:22
 * @Description
 */
public abstract class Tool<T> {
    @Resource
    protected T toolService;
}
