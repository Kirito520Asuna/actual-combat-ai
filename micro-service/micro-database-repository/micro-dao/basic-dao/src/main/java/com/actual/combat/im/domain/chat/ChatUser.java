package com.actual.combat.im.domain.chat;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.basic.core.enums.im.ChatType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:38
 * @Description
 */

/**
 * 聊天窗口关联用户
 */
@Schema(description = "聊天窗口关联用户")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.chat_user)
public class ChatUser implements Serializable {
    @TableId(value = "`id`",type = IdType.AUTO)
    @Schema(description = "")
    @NotNull(message = "不能为null")
    private Long id;

    /**
     * 聊天窗口id
     */
    @TableField(value = "`chat_id`")
    @Schema(description = "聊天窗口id")
    @NotNull(message = "聊天窗口id不能为null")
    private Long chatId;

    /**
     * 类型 群聊|私聊
     */
    @TableField(value = "`chat_type`")
    @Schema(description = "类型 群聊|私聊")
    @Size(max = 255, message = "类型 群聊|私聊最大长度要小于 255")
    @Enumerated(value = EnumType.STRING)
    private ChatType chatType = ChatType.ONE_ON_ONE_CHAT;

    /**
     * 用户id
     */
    @TableField(value = "`user_id`")
    @Schema(description = "用户id")
    @NotNull(message = "用户id不能为null")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "`create_time`")
    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}