package com.actual_combat.base.exceptions;


import com.actual_combat.base.enums.ApiCode;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2023/10/23 0023 15:06
 * @Description
 */
@Getter
public class GlobalCustomException extends GlobalException {

    public GlobalCustomException(ApiCode apiCode) {
        super(apiCode.getCode(),apiCode.getMessage());
    }

    public GlobalCustomException(int code, String message) {
        super(code,message);
    }

    public GlobalCustomException(ApiCode apiCode, String message) {
        super(apiCode.getCode(),message);
    }

    public GlobalCustomException(String message) {
        super(ApiCode.FAIL.getCode(),message);
    }

    public GlobalCustomException(int code) {
        super(code);
    }
}
