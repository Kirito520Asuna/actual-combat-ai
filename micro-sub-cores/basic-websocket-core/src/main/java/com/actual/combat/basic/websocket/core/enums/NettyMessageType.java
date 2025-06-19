package com.actual.combat.basic.websocket.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author yan
 * @Date 2023/8/18 0018 17:10
 * @Description
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum NettyMessageType {
    LOGIN("1","登录"),
    SEND("2","发送消息"),
    CALL("3","发起视频通话"),
    RESPONSE("4","接收视频通话"),
    ASKOFF("5","传递请求off"),
    GETOFF("6","获取off"),
    ANSWER("7","answer"),
    CANDIDATE("8","candidate")
    ;
    private String type;
    private String desc;
}
