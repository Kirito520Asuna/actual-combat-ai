package com.actual.combat.im.domain.im;

import com.actual.combat.basic.core.constant.table.TableConstants;
import com.actual.combat.basic.core.enums.im.MessageType;
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
 * @Date 2023/8/7 0007 10:39
 * @Description
 */
@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = TableConstants.message)
public class Message implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "")
    @NotNull(message = "不能为null")
    private Long id;

    /**
     * 来源id
     */
    @TableField(value = "`from`")
    @Schema(description = "来源id")
    private Long from;

    /**
     * 给谁
     */
    @TableField(value = "`to`")
    @Schema(description = "给谁")
    private Long to;

    /**
     * 类型
     */
    @TableField(value = "`type`")
    @Schema(description = "类型  ")
    @Enumerated(value = EnumType.STRING)
    private MessageType type = MessageType.TXT;

    @TableField(value = "`content`")
    @Schema(description = "")
    @Size(max = 255, message = "最大长度要小于 255")
    private String content;

    @TableField(value = "`time`")
    @Schema(description = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private static final long serialVersionUID = 1007018913166779763L;
}