package com.actual.combat.basic.exceptions;

import com.actual.combat.basic.enums.ApiCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/5/18 14:10:38
 * @Description
 */
@AllArgsConstructor
@Getter
public class GlobalException extends RuntimeException {
    Integer code = ApiCode.FAIL.getCode();
    public GlobalException(String message) {
        super(message);
    }

    public GlobalException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
