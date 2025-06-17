package com.actual.combat.basic.core.enums.im;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2023/8/10 0010 17:25
 * @Description
 */
@AllArgsConstructor
@Getter
public enum MessageType {
    TXT("文本"), IMAGE("图片");

    private String desc;
}
