package com.actual.combat.basic.websocket.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author yan
 * @Date 2025/2/13 2:41:03
 * @Description
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    system("系统消息"), text("文本消息"),image("图片消息"),
    voice("语音消息"),video("视频消息"),file("文件消息"),
    location("位置消息"),custom("自定义消息");
    private String desc;
    MessageType() {
        this.desc = "";
    }
}
