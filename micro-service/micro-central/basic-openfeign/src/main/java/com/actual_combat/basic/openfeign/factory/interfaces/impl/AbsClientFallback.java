package com.actual_combat.basic.openfeign.factory.interfaces.impl;

import com.actual_combat.basic.openfeign.factory.AbsEnum;
import com.actual_combat.basic.openfeign.factory.interfaces.AbsClient;

/**
 * @Author yan
 * @Date 2024/5/14 0014 13:26
 * @Description
 */
public class AbsClientFallback implements AbsClient {
    @Override
    public boolean support(AbsEnum absEnum) {
        return AbsClient.super.support(absEnum);
    }
}
