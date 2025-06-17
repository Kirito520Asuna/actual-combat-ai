package com.actual.combat.aop.all.aviator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author yan
 * @Date 2024/11/12 下午2:59:52
 * @Description
 */
@Data @Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AviatorValidInfo {
    private String preconditionExpression= "true";
    private boolean throwException = true;
    private String expression;
    private String errorMessage;
}
