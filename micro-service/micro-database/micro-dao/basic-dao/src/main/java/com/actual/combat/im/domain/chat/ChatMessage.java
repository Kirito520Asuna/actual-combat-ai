package com.actual.combat.im.domain.chat;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.basic.core.enums.im.MessageType;
import com.actual.combat.basic.core.view.BaseJsonView;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */

/**
 * 聊天窗口关联消息
 */
@Schema(description = "聊天窗口关联消息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.chat_message)
public class ChatMessage implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "")
    @NotNull(message = "不能为null", groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.ChatView.class})
    private Integer id;

    /**
     * 聊天窗口id
     */
    @TableField(value = "`chat_id`")
    @Schema(description = "聊天窗口id")
    @NotNull(message = "聊天窗口id不能为null")
    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.SendMessageView.class})
    private Long chatId;

    /**
     * 消息id
     */
    @TableField(value = "`message_id`")
    @Schema(description = "消息id")
    @NotNull(message = "消息id不能为null", groups = {BaseJsonView.UpdateView.class})
    @JsonView(value = {BaseJsonView.ChatView.class})
    private Long messageId;
    @TableField(exist = false)
    @Schema(description = "发送人id")
    @NotNull(groups = {BaseJsonView.SendMessageView.class})
    @JsonView(value = {BaseJsonView.SendMessageView.class})
    private Long sendUserId;
    @TableField(exist = false)
    @Schema(description = "发送人")
    @JsonView(value = {BaseJsonView.ChatView.class})
    private UserVo sendUser;
    @TableField(exist = false)
    @Schema(description = "内容")
    @NotNull(groups = {BaseJsonView.SendMessageView.class})
    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.SendMessageView.class})
    private String content;
    @TableField(exist = false)
    @Schema(description = "类型")
    @NotNull(groups = {BaseJsonView.SendMessageView.class})
    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.SendMessageView.class})
    @Enumerated(value = EnumType.STRING)
    private MessageType type;
    @TableField(exist = false)
    @Schema(description = "时间")
    @JsonView(value = {BaseJsonView.ChatView.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}