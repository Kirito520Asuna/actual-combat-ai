package com.actual.combat.basic.core.enums.im;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:42
 * @Description
 */
@AllArgsConstructor
@Getter
public enum ChatType {
    ONE_ON_ONE_CHAT("一对一"), GROUP_CHAT("群聊");
    private String desc;
}
