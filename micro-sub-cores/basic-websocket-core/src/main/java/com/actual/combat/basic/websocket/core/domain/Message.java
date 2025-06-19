package com.actual.combat.basic.websocket.core.domain;

/**
 * @Author yan
 * @Date 2025/2/4 1:48:04
 * @Description
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.actual.combat.basic.websocket.core.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * WebSocket 消息实体类
 */
@Data @NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Message {

    // 消息类型常量（可根据业务扩展）
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_SYSTEM = "system";
    @JsonIgnore
    //消息发送者实例ID
    private String sendInstanceId;
    @JsonIgnore
    //消息接收者实例ID
    private String targetInstanceId;

    // 消息发送者ID（例如用户ID）
    private String senderId;

    // 消息接收者ID（用户ID、群组ID或频道ID）
    private String targetId;

    // 消息内容（文本、JSON或Base64编码的二进制数据）
    private String content;

    // 消息类型（text/image/system...）
    private String type = MessageType.text.name();

    // 消息时间戳
    private Long timestamp = System.currentTimeMillis();


}