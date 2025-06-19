package com.actual.combat.basic.user.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.core.constant.Roles;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.user.core.service.SysRoleService;
import com.actual.combat.basic.user.core.service.SysUserRoleService;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.user.domain.SysRole;
import com.actual.combat.user.domain.SysUserRole;
import com.actual.combat.user.mapper.SysRoleDao;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRole> implements SysRoleService {
    @Resource
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<String> getRoleKeys(List<Long> rolesId) {
        List<String> roleKeys = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(rolesId)) {
            List<String> keys = listByIds(rolesId)
                    .stream().map(SysRole::getRoleKey)
                    .map(o -> o.startsWith(Roles.roles) ? o : new StringBuffer(Roles.roles).append(o).toString())
                    .collect(Collectors.toList());
            roleKeys.addAll(keys);
        }
        return roleKeys;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public SysRole getByUserId(Long userId) {
        SysUserRole sysUserRole = sysUserRoleService.getById(userId);
        if (ObjectUtils.isNull(sysUserRole)){
            throw new GlobalCustomException("用户未分配角色");
        }
        SysRole sysRole = getById(sysUserRole.getRoleId());
        String roleKey = sysRole.getRoleKey();
        sysRole.setRoleKey(roleKey.startsWith(Roles.roles) ? roleKey : new StringBuffer(Roles.roles).append(roleKey).toString());
        return sysRole;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysRole> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysRole> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysRole> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysRole> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysRole record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysRole record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
