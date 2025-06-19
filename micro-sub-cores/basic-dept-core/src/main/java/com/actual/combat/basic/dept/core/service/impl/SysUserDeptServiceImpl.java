package com.actual.combat.basic.dept.core.service.impl;

import com.actual.combat.dept.domain.SysUserDept;
import com.actual.combat.dept.mapper.SysUserDeptMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.dept.core.service.SysUserDeptService;
@Service
public class SysUserDeptServiceImpl extends ServiceImpl<SysUserDeptMapper, SysUserDept> implements SysUserDeptService{
    @Override
    public List<SysUserDept> getByIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysUserDept> query = Wrappers.lambdaQuery(SysUserDept.class);
        query.in(SysUserDept::getDeptId,deptIds);
        return list(query);
    }


}
