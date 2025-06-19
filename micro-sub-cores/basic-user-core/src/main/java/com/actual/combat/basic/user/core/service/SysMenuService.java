package com.actual.combat.basic.user.core.service;

import java.util.List;
import java.util.Map;

import com.actual.combat.user.domain.SysMenu;
import com.actual.combat.user.dto.SysMenuDto;
import com.actual.combat.vo.SysMenuTreeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 获取菜单树
     * @return
     */
    List<SysMenuTreeVo> getAnyTreeList();

    /**
     * 获取用户菜单树
     * @param userId
     * @return
     */

    List<SysMenuTreeVo> selectMenuTreeByUserId(Long userId);

    @Transactional(rollbackFor = Exception.class)
    SysMenuDto add(SysMenuDto dto);

    @Transactional(rollbackFor = Exception.class)
    SysMenuDto updateBus(SysMenuDto dto);

    @Transactional(rollbackFor = Exception.class)
    boolean delete(List<Integer> ids);

    @Transactional(rollbackFor = Exception.class)
    boolean deleteCustom(Integer id);

    List<SysMenu> selectByCustomMap(Map<String, Object> map);

    /**
     * 新增菜单 并向祖先表中插入数据
     * @param sysMenu
     */
    void addCustom(SysMenu sysMenu);
    void updateCustom(SysMenu sysMenu);

    int updateBatch(List<SysMenu> list);

    int updateBatchSelective(List<SysMenu> list);

    int batchInsert(List<SysMenu> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysMenu> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysMenu record);

    int insertOrUpdateSelective(SysMenu record);
    /**
     * 导入菜单权限数据
     *
     * @param sysMenuList 菜单权限数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importDataSysMenu(List<SysMenu> sysMenuList,Boolean isUpdateSupport, String operName);
    /**
     * 删除业务信息
     *
     * @param menuIds 需要删除的表数据List
     * @return 结果
     */
    boolean deleteByMenuIds(List<Long> menuIds);
    /**
     * 查询业务信息
     *
     * @param menuId
     * @return 结果
     */
    SysMenu selectSysMenuByMenuId(Long menuId);
    /**
     * 查询业务信息列表
     *
     * @param sysMenu 菜单权限
     * @return 业务信息集合
     */
    List<SysMenu> selectSysMenuList(SysMenu sysMenu);
}
