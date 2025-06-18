package com.actual.combat.basic.openfeign.factory.interfaces.impl;

import com.actual.combat.basic.openfeign.factory.AbsEnum;
import com.actual.combat.basic.openfeign.factory.interfaces.OrderClient;

/**
 * @Author yan
 * @Date 2024/5/14 0014 13:26
 * @Description
 */
public class OrderClientFallback implements OrderClient {
    @Override
    public boolean support(AbsEnum absEnum) {
        return OrderClient.super.support(absEnum);
    }
}
