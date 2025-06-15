package com.actual_combat.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2024/9/28 下午11:03:26
 * @Description
 */
@Getter @AllArgsConstructor
public enum Openfeign {
    OPENFEIGN("X-HEADER-OPENFEIGN","openfeign",true);
    private String header;
    private String desc;
    private boolean aBoolean;
}
