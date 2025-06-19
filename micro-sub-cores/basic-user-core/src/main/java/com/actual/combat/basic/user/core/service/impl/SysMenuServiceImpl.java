package com.actual.combat.basic.user.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.utils.tree.TreeUtils;
import com.actual.combat.basic.core.config.auth.AuthorizationConfig;
import com.actual.combat.basic.enums.ApiCode;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.user.core.service.SysMenuAncestorService;
import com.actual.combat.basic.user.core.service.SysMenuService;
import com.actual.combat.basic.user.core.service.SysRoleService;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.tomapper.SysMenuMapper;
import com.actual.combat.user.domain.SysMenu;
import com.actual.combat.user.domain.SysMenuAncestor;
import com.actual.combat.user.domain.SysRole;
import com.actual.combat.user.dto.SysMenuDto;
import com.actual.combat.user.mapper.SysMenuDao;
import com.actual.combat.vo.SysMenuTreeVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenu> implements SysMenuService {
    @Resource
    private SysMenuAncestorService sysMenuAncestorService;

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public List<SysMenuTreeVo> getAnyTreeList() {
        List<SysMenuTreeVo> treeVoList = list().stream().map(SysMenuMapper::mapperToTreeVo).collect(Collectors.toList());
        List<SysMenuTreeVo> treeVos = TreeUtils.listToTree(treeVoList, SysMenuTreeVo.class);
        return treeVos;
    }

    @Override
    public List<SysMenuTreeVo> selectMenuTreeByUserId(Long userId) {
        SysRole sysRole = sysRoleService.getByUserId(userId);
        List<SysMenuTreeVo> treeVoList;
        if (ObjectUtils.equals(sysRole.getRoleKey(), SpringUtil.getBean(AuthorizationConfig.class).getAdminKey())) {
            treeVoList = list().stream().map(SysMenuMapper::mapperToTreeVo).collect(Collectors.toList());
        } else {
            treeVoList = baseMapper.selectMenuTreeByUserId(userId).stream().map(SysMenuMapper::mapperToTreeVo).collect(Collectors.toList());
        }
        List<SysMenuTreeVo> treeVos = TreeUtils.listToTree(treeVoList, SysMenuTreeVo.class);
        return treeVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMenuDto add(SysMenuDto dto) {
        SysMenu sysMenu = SysMenuMapper.mapperToDao(dto);
        Long parentId = sysMenu.getParentId();

        if (ObjectUtil.isNotEmpty(parentId)) {
            SysMenu byId = getById(parentId);
            if (ObjectUtil.isEmpty(byId)) {
                throw new GlobalCustomException("不存在父！");
            }
        }
        save(sysMenu);
        Long menuId = sysMenu.getMenuId();
        //祖先表
        List<SysMenuAncestor> ancestors = new ArrayList<>();
        SysMenuAncestor self = new SysMenuAncestor().setMenuId(menuId).setMenuParentId(menuId).setLevel(0l);
        ancestors.add(self);
        if (ObjectUtil.isNotEmpty(parentId)) {
            SysMenuAncestor ancestor = new SysMenuAncestor().setMenuId(menuId).setMenuParentId(parentId).setLevel(1l);
            ancestors.add(ancestor);
        }
        sysMenuAncestorService.saveBatch(ancestors);
        return SysMenuMapper.mapperToDto(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMenuDto updateBus(SysMenuDto dto) {
        //String menuId = "menuId";
        String menuIdName = StrUtil.toCamelCase(SysMenuAncestor.COL_MENU_ID);
        String levelGtName = new StringBuffer(SysMenuAncestor.COL_LEVEL).append("Gt").toString();
        SysMenu sysMenu = SysMenuMapper.mapperToDao(dto);
        //祖先表
        Long menuId = sysMenu.getMenuId();
        Long parentId = sysMenu.getParentId();
        Map<String, Object> hashMap = new LinkedHashMap<>();
        hashMap.put(menuIdName, menuId);
        List<SysMenuAncestor> ancestorList = sysMenuAncestorService.selectByCustomMap(hashMap);
        List<Long> paIds = CollUtil.isEmpty(ancestorList) ? new ArrayList<>() : ancestorList.stream().map(SysMenuAncestor::getMenuParentId).collect(Collectors.toList());
        SysMenu primaryKey = getById(menuId);

        if (ObjectUtil.isNotEmpty(parentId)) {
            SysMenu parentIdPrimaryKey = getById(parentId);
            if (ObjectUtil.isEmpty(parentIdPrimaryKey)) {
                parentId = primaryKey.getParentId();
                dto.setParentId(parentId);
            }
        }
        if (paIds.contains(parentId) && (!ObjectUtil.equals(primaryKey.getParentId(), parentId))) {
            throw new GlobalCustomException("非法操作！父不可移到子级以下！");
        }

        Map<String, Object> toMap = BeanUtil.beanToMap(dto);
        toMap.put("parentBool", true);
        baseMapper.updateByMap(toMap);

        if (ObjectUtil.isNotEmpty(parentId)) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(menuIdName, parentId);
            //map.put("levelNeq", 0);
            map.put(new StringBuffer(SysMenuAncestor.COL_LEVEL).append("OrderAsc").toString(), true);
//            map.put("limitOne",true);
//            map.put("level",1);
            //1-》10 下 删除1 的所有父级 在插入 10 的所有父级 到1 并将level +1

            Map<String, Object> deleteMap = new LinkedHashMap<>();
            deleteMap.put(menuIdName, menuId);
            deleteMap.put(levelGtName, 1);
            //移除原有祖先 插入新祖先
            sysMenuAncestorService.deleteByCustomMap(deleteMap);
            List<SysMenuAncestor> ancestors = sysMenuAncestorService.selectByCustomMap(map);
            if (CollUtil.isNotEmpty(ancestors)) {
                ancestors.stream().forEach(one -> one.setId(null).setMenuParentId(menuId).setLevel(one.getLevel() + 1));
                sysMenuAncestorService.saveBatch(ancestors);
            } else {
                //不存在祖先
                SysMenuAncestor treeAncestor = new SysMenuAncestor().setMenuId(menuId).setMenuParentId(parentId).setLevel(1l);
                sysMenuAncestorService.save(treeAncestor);
            }
        } else {
            Map<String, Object> deleteMap = new LinkedHashMap<>();
            deleteMap.put(menuIdName, menuId);
            deleteMap.put(levelGtName, 1);
            //移除原有祖先
            sysMenuAncestorService.deleteByCustomMap(deleteMap);
        }
        primaryKey = getById(menuId);
        return SysMenuMapper.mapperToDto(primaryKey);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(List<Integer> ids) {
        boolean bool = true;
        for (Integer id : ids) {
            bool = bool && deleteCustom(id);
        }
        return bool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustom(Integer id) {
        //String menuParentId = "menuParentId";
        //String parentIdName = "parentId";
        String menuParentId = StrUtil.toCamelCase(SysMenuAncestor.COL_MENU_PARENT_ID);
        String parentIdName = StrUtil.toCamelCase(SysMenu.COL_PARENT_ID);
//        String limitOneName = "limitOne";
//        String treeIdName = "treeId";
        String levelGtName = new StringBuffer(SysMenuAncestor.COL_LEVEL).append("Gt").toString();
        boolean bool = true;
        Map<String, Object> map = new LinkedHashMap<>();

        map.put(menuParentId, id);
        //查询到所有
        List<SysMenuAncestor> ancestors = sysMenuAncestorService.selectByCustomMap(map);
        map = new LinkedHashMap<>();
        map.put(parentIdName, id);
        //主表
        List<SysMenu> sysMenus = selectByCustomMap(map);
        if (CollUtil.isNotEmpty(sysMenus)) {
            sysMenus.stream().forEach(one -> one.setParentId(null));
            //更新主表 删除
            bool = bool && (updateBatch(sysMenus) > 0);
        }
        bool = bool && removeById(id);
        //祖先数据不为空
        if (CollUtil.isNotEmpty(ancestors)) {
            //子id
            List<Long> deleteTrees = ancestors.stream().map(SysMenuAncestor::getMenuId).distinct().collect(Collectors.toList());
            //子id 父ID 和级别>= 删除
            //以子 循环删除
            for (Long deleteTreeId : deleteTrees) {
                map = new LinkedHashMap<>();
                map.put(StrUtil.toCamelCase(SysMenuAncestor.COL_MENU_ID), deleteTreeId);
                map.put(menuParentId, id);
                map.put("limitOne", true);
                List<SysMenuAncestor> ancestorList = sysMenuAncestorService.selectByCustomMap(map);
                SysMenuAncestor ancestor = ancestorList.get(0);
                map.put(levelGtName, ancestor.getLevel());
                int rows = sysMenuAncestorService.deleteByCustomMap(map);
                bool = bool && (rows > 0);
            }
        }
        return bool;
    }

    @Override
    public List<SysMenu> selectByCustomMap(Map<String, Object> map) {
        return baseMapper.selectByCustomMap(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustom(@Valid @NotNull SysMenu sysMenu) {
        Long parentId = sysMenu.getParentId();
        if (ObjectUtil.isNotEmpty(parentId)) {
            Optional.ofNullable(getById(parentId)).filter(ObjectUtil::isNotEmpty).orElseThrow(() -> new GlobalCustomException("不存在父！"));
        }

        save(sysMenu);
        //祖先表
        List<SysMenuAncestor> ancestors = CollUtil.newArrayList();
        Long menuId = sysMenu.getMenuId();
        SysMenuAncestor self = new SysMenuAncestor().setMenuId(menuId).setMenuParentId(menuId).setLevel(0l);
        ancestors.add(self);
        if (ObjectUtil.isNotEmpty(parentId)) {
            Long selfLevel = self.getLevel();
            SysMenuAncestor ancestor = new SysMenuAncestor().setMenuId(menuId).setMenuParentId(parentId).setLevel(selfLevel + 1);
            ancestors.add(ancestor);
        }
        sysMenuAncestorService.saveBatch(ancestors);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void updateCustom(@Valid @NotNull SysMenu sysMenu) {
        Long menuId = sysMenu.getMenuId();
        if (ObjectUtil.isEmpty(menuId)) {
            throw new GlobalCustomException(ApiCode.VALIDATE_FAILED);
        }
        SysMenu menu = getById(menuId);
        if (ObjectUtil.isEmpty(menu)) {
            throw new GlobalCustomException(ApiCode.VALIDATE_FAILED);
        }
        Long parentId = sysMenu.getParentId();
        Long menuParentId = menu.getParentId();
        if (ObjectUtil.isNotEmpty(parentId) && ObjectUtil.isNotEmpty(menuParentId) && !ObjectUtil.equals(parentId, menuParentId)) {

        }

        updateById(sysMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysMenu> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysMenu> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysMenu> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysMenu> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysMenu record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysMenu record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    /**
     * 导入菜单权限数据
     *
     * @param sysMenuList     菜单权限数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importDataSysMenu(List<SysMenu> sysMenuList, Boolean isUpdateSupport, String operName) {
        // 待实现
        isUpdateSupport = ObjectUtils.defaultIfEmpty(isUpdateSupport, false);
        List<SysMenu> saveList = sysMenuList;
        if (isUpdateSupport) {
            List<SysMenu> updateList = sysMenuList.stream().filter(o -> ObjectUtil.isNotEmpty(o.getMenuId())).collect(Collectors.toList());
            saveList = sysMenuList.stream().filter(o -> ObjectUtil.isEmpty(o.getMenuId())).collect(Collectors.toList());
            updateBatch(updateList);
        }
        if (CollUtil.isNotEmpty(saveList)) {
            saveBatch(saveList);
        }
        return "ok";
    }

    /**
     * 删除业务信息
     *
     * @param menuIds 需要删除的表数据List
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMenuIds(List<Long> menuIds) {
        boolean removeByIds = removeByIds(menuIds);
        return removeByIds;
    }

    /**
     * 查询业务信息
     *
     * @param menuId
     * @return 结果
     */
    @Override
    public SysMenu selectSysMenuByMenuId(Long menuId) {
        return getById(menuId);
    }

    /**
     * 查询业务信息列表
     *
     * @param sysMenu 菜单权限
     * @return 业务信息集合
     */
    @Override
    public List<SysMenu> selectSysMenuList(SysMenu sysMenu) {
        return list();
    }
}
