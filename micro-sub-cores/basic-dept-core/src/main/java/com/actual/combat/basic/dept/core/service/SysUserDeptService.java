package com.actual.combat.basic.dept.core.service;

import com.actual.combat.dept.domain.SysUserDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserDeptService extends IService<SysUserDept>{


    List<SysUserDept> getByIds(List<Long> deptIds);

}
