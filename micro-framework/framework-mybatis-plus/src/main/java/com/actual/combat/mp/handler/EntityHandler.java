package com.actual.combat.mp.handler;

import com.actual.combat.mp.abs.handler.AbsEntityHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;


/**
 * @Author yan
 * @Date 2024/5/22 0022 17:40
 * @Description
 */
@Component
@ConditionalOnMissingBean(AbsEntityHandler.class)
public class EntityHandler implements AbsEntityHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = getUserId();
        AbsEntityHandler.super.insertFill(metaObject);
        this.strictInsertFill(metaObject, "createBy", () -> userId, String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String userId = getUserId();
        AbsEntityHandler.super.updateFill(metaObject);
        this.strictInsertFill(metaObject, "updateBy", () -> userId, String.class);
    }


}
