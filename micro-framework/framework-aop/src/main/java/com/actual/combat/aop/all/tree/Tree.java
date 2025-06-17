package com.actual.combat.aop.all.tree;

import java.lang.annotation.*;

/**
 * @author yan
 * @date 2024/2/29 1:17
 */
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tree {
    /**
     * id 名称
     * @return
     */
    boolean id() default false;

    /**
     * 父id 名称
     * @return
     */
    boolean parentId() default false;

    /**
     * 子集 名称
     * @return
     */
    boolean subset() default false;
}
