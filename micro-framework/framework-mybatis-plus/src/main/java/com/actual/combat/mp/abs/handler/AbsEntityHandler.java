package com.actual.combat.mp.abs.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.mp.abs.service.MpUserService;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @Author yan
 * @Date 2024/5/22 0022 17:42
 * @Description
 */
public interface AbsEntityHandler extends MetaObjectHandler {
    @Override
    default void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
    }

    @Override
    default void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
    }

    // 定义一个名为 getUserId 的方法
    default String getUserId() {
        return SpringUtil.getBean(MpUserService.class).getUserId();
    }
}
