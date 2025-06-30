package com.minimalism.mybatis.abs.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.mybatis.abs.service.MpUserService;

/**
 * @Author yan
 * @Date 2024/5/22 0022 17:42
 * @Description
 */
public interface AbsEntityHandler  {

    // 定义一个名为 getUserId 的方法
    default String getUserId() {
        return SpringUtil.getBean(MpUserService.class).getUserId();
    }
}
