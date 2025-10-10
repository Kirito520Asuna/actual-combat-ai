package com.actual.combat.aop.validator;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.actual.combat.aop.all.validator.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @Author yan
 * @Date 2025/10/10 13:48:08
 * @Description
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        return PhoneUtil.isPhone(phone);
    }
}
