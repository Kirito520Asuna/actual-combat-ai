package com.actual_combat.base.exceptions;

import com.actual_combat.base.enums.ApiCode;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/3/7 19:43:00
 * @Description
 */
@Getter
public class GlobalConfigException extends GlobalException {
     Integer code;

    public GlobalConfigException() {
        super(ApiCode.SERVICE_CONFIG.getCode(),ApiCode.SERVICE_CONFIG.getMessage());
    }

    public GlobalConfigException(String message) {
        super(ApiCode.SERVICE_CONFIG.getCode(),message);
    }
}
