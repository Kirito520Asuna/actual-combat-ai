package com.actual.combat.basic.core.vo.user;

import com.actual.combat.basic.core.view.BaseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2025/2/13 13:28:37
 * @Description
 */
@Data @NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    private static final long serialVersionUID = -2839454712692611799L;
    @Schema(description = "")
    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
    private Long userId;

    @Schema(description = "账号")
    @JsonView(value = {BaseJsonView.LoginView.class,
            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class})
    private String userName;
    @Schema(description = "昵称")
    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class,
            BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.ApplyView.class, BaseJsonView.UserView.class,BaseJsonView.ApplyAgreeView.class})
    private String nickName;

    @Schema(description = "")
    @JsonView(value = {BaseJsonView.LoginView.class, BaseJsonView.RegisterView.class})
    private String password;
    @Schema(description = "")
    @JsonView(value = {BaseJsonView.RegisterView.class})
    private String password2;

    @Schema(description = "")
    @JsonView(value = {BaseJsonView.WebView.class, BaseJsonView.ChatView.class,
            BaseJsonView.UserView.class,BaseJsonView.ApplyView.class})
    private String avatar;
    @Schema(description = "聊天窗口id")
    @JsonView(value = {BaseJsonView.WebView.class,BaseJsonView.ApplyAgreeView.class,BaseJsonView.UserChatView.class})
    private Long chatId;
    @Schema(description = "申请id")
    @JsonView(value = {BaseJsonView.ApplyView.class})
    private Long applyId;
    @Schema(description = "是否是自己")
    @JsonView(value = {BaseJsonView.ChatView.class, BaseJsonView.UserView.class})
    private Boolean isSelf = false;

    @Schema(description = "是否是好友")
    @JsonView(value = {BaseJsonView.UserView.class})
    private Boolean isFriend = false;
    @Schema(description = "是否已申请")
    @JsonView(value = {BaseJsonView.UserView.class})
    private Boolean isApply = false;
}
