package com.actual.combat.websocket.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.aop.all.security.Login;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.websocket.core.domain.Message;
import com.actual.combat.basic.websocket.core.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * @Author minimalism
 * @Date 2025/2/6 1:50:07
 * @Description
 */
@Tag(name = "消息")
@RequestMapping({"/message", "/api/message"})
@RestController
public class MessageController {
    @GetMapping
    public Result<String> test() {
        return Result.ok();
    }
    @SysLog
    @Login
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<String> sendLocalMessage(@RequestBody Message message) {
        SpringUtil.getBean(MessageService.class).sendLocalMessage(message.getSenderId(),message);
        return Result.ok();
    }
}
