package com.actual_combat.aop.all.pdf;

import cn.hutool.core.date.DatePattern;
import com.actual_combat.aop.enums.PDFType;

import java.lang.annotation.*;

/**
 * @Author yan
 * @Date 2023/11/29 0029 18:12
 * @Description
 */
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PDFField{
    /**
     * 对PDF映射名
     *
     * @return
     */
    String[] values() default {};
    /**
     * 类型
     * @return
     */
    PDFType type() default PDFType.TEXT;

    /**
     * 是时间类型
     * @return
     */
    boolean isTime() default false;

    /**
     * 时间格式
     * @return
     */
    String[] datePatterns() default {DatePattern.NORM_DATETIME_PATTERN};
}
