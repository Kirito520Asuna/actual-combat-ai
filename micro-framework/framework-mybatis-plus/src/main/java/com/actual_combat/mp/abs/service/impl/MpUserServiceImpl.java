package com.actual_combat.mp.abs.service.impl;

import com.actual_combat.mp.abs.handler.AbsEntityHandler;
import com.actual_combat.mp.abs.service.MpUserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2025/3/6 23:25:17
 * @Description
 */
@Service
@ConditionalOnBean(AbsEntityHandler.class)
public class MpUserServiceImpl implements MpUserService {
}
