package com.actual.combat.basic.dept.core.service;

import java.util.List;

import com.actual.combat.dept.domain.SysDept;
import com.actual.combat.vo.dept.DeptTreeVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysDeptService extends IService<SysDept>{


    int updateBatch(List<SysDept> list);

    int updateBatchUseMultiQuery(List<SysDept> list);

    int updateBatchSelective(List<SysDept> list);

    int batchInsert(List<SysDept> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDept> list);

    int insertOrUpdate(SysDept record);

    int insertOrUpdateSelective(SysDept record);

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 校验部门是否有数据权限
     *
     * @param deptIds 部门id
     */
    void checkDeptDataScope(List<Long> deptIds);

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptIds 部门IDS
     * @return 结果
     */
    boolean hasChildByDeptIds(List<Long> deptIds);

    /**
     * 查询部门是否存在用户
     *
     * @param deptIds 部门IDS
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(List<Long> deptIds);

    /**
     *
     * @param deptIds
     * @return
     */
    boolean deleteByDeptIds(List<Long> deptIds);

    /**
     * 校验部门名称是否唯一
     * @param dept
     * @return
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 查询子部门ID
     * @param deptId
     * @return
     */
    int selectNormalChildrenDeptById(Long deptId);

    List<DeptTreeVo> selectTree(List<Long> ids);

    /**
     *
     * @param dept
     * @return
     */
    boolean updateDept(SysDept dept);

    /**
     * 新增部门信息
     * @param dept
     * @return
     */
    boolean saveDept(SysDept dept);
}
