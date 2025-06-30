package com.minimalism.mybatis.handler;

import com.minimalism.mybatis.abs.handler.AbsEntityHandler;
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

}
