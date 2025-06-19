package com.actual.combat.basic.utils.enums;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Field;

/**
 * @Author yan
 * @Date 2024/6/7 0007 17:06
 * @Description
 */
public class EnumUtils extends EnumUtil{
    /**
     * 获取枚举类中指定字段的值--->私有字段 获取枚举类型的私有字段
     *
     * @param enumConstant
     * @param fieldName
     * @return
     * @param <T>
     */
    public static <T extends Enum<T>> Object getPrivateField(Enum<T> enumConstant, String fieldName) {
        try {
            Field field = enumConstant.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(enumConstant);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取枚举类中指定字段的值--->私有字段 获取枚举类型的私有字段根据枚举类型的私有字段获取枚举类型
     *
     * @param enumClass
     * @param code
     * @param fieldName
     * @return
     * @param <T>
     * @param <C>
     */
    public static <T extends Enum<T>, C> T getEnumByPrivateFieldName(Class<T> enumClass, C code, String fieldName) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            C codePrivateField = (C) getPrivateField(enumConstant, fieldName);
            if (ObjectUtil.equals(codePrivateField, code)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No matching enum for " + fieldName + ": " + code);
    }
}
