package com.actual.combat.basic.websocket.core.domain.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2023/8/18 0018 17:05
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyMessage implements Serializable {
    // "接收"
    private String to;
    // "发送"
    private String from;
    //"消息类型"
    private String type;
    //"唯一值"
    private String uuid = UUID.randomUUID().toString();
    //"消息"
    private String message ;
}
