package com.actual.combat.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/3/9 16:12:44
 * @Description
 */
@AllArgsConstructor
@Getter
public enum OSType {
    win("\\"), linux("/"), mac("/");
    private String separator;

}
