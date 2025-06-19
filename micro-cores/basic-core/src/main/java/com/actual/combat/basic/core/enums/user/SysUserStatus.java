package com.actual.combat.basic.core.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2024/9/26 下午8:21:46
 * @Description
 */
@AllArgsConstructor @Getter
public enum SysUserStatus {
    normal("0",true,"正常"),stopUsing("1",false,"停用")
    ;
    private String status;
    private boolean statusBoolean;
    private String desc;

}
