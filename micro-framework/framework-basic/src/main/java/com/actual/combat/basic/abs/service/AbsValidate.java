package com.actual.combat.basic.abs.service;


import cn.hutool.core.exceptions.ValidateException;

public interface AbsValidate<T> {
    /**
     * 校验是否通过
     *
     * @param t
     * @return
     */
    boolean validateOk(T t);

    default boolean validateOk() {
        boolean validateOk = validateOk((T) this);
        if (!validateOk) {
            throw new ValidateException();
        }
        return validateOk;
    }
}
