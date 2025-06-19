package com.actual.combat.basic.user.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.user.domain.SysMenu;
import com.actual.combat.user.domain.SysRoleMenu;
import com.actual.combat.user.mapper.SysRoleMenuDao;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.actual.combat.basic.user.core.service.SysMenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;

import com.actual.combat.basic.user.core.service.SysRoleMenuService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenu> implements SysRoleMenuService {
    @Resource
    private SysMenuService sysMenuService;
    @Override
    public List<String> getPermsKeys(List<Long> rolesId) {
        List<String> keys = CollUtil.newArrayList();
        if (CollUtil.isEmpty(rolesId)) {
            return keys;
        }
        List<Long> menuIds = list(Wrappers.lambdaQuery(SysRoleMenu.class).select(SysRoleMenu::getMenuId).in(SysRoleMenu::getRoleId, rolesId))
                .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(menuIds)){
            List<String> permsList = sysMenuService.listByIds(menuIds)
                    .stream().map(SysMenu::getPerms).collect(Collectors.toList());
            keys.addAll(permsList);
        }
        return keys;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysRoleMenu> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysRoleMenu> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysRoleMenu> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysRoleMenu> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysRoleMenu record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysRoleMenu record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
