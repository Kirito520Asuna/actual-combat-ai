package com.actual.combat.basic.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author yan
 * @Date 2023/5/26 0026 15:25
 * @Description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Header {
    TOKEN("token","TOKEN"),
    AUTHORIZATION("Authorization",""),
    SIGN("Sign","SIGN"),
    TIMESTAMP("Timestamp","TIMESTAMP");
    private String name;
    private String description;
}
