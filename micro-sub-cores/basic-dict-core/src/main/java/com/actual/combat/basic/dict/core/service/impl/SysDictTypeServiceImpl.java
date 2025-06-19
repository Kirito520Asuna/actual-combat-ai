package com.actual.combat.basic.dict.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.core.constant.user.UserConstants;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.dict.domain.SysDictData;
import com.actual.combat.dict.domain.SysDictType;
import com.actual.combat.dict.mapper.SysDictTypeMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.actual.combat.basic.dict.core.service.SysDictDataService;
import com.actual.combat.basic.dict.core.utils.DictUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import com.actual.combat.basic.dict.core.service.SysDictTypeService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {
    @Resource
    private SysDictDataService dictDataService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysDictType> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchUseMultiQuery(List<SysDictType> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysDictType> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysDictType> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysDictType> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOnDuplicateUpdate(SysDictType record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOnDuplicateUpdateSelective(SysDictType record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        Long dictId = dictType.getDictId();
        boolean unique = UserConstants.UNIQUE;
        if (ObjectUtils.isNull(dictId)) {
            SysDictType dictTypeUnique = baseMapper.checkDictTypeUnique(dictType.getDictType());
            if (!ObjectUtils.equals(dictTypeUnique.getDictId(), dictId)) {
                unique = UserConstants.NOT_UNIQUE;
            }
        }
        return unique;
    }

    @Override
    public List<SysDictType> selectSysDictTypeList(SysDictType dictType) {
        return baseMapper.selectDictTypeList(dictType);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteByDictIds(List<Long> dictIds) {
        if (CollUtil.isNotEmpty(dictIds)) {
            List<SysDictType> dictTypes = listByIds(dictIds);
            for (SysDictType dictType : dictTypes) {
                int count = (int) count(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dictType.getDictType()));
                if (count > 0) {
                    throw new BusinessException(String.format("%s已分配,不能删除", dictType.getDictName()));
                }
                removeById(dictType.getDictId());
                DictUtils.removeDictCache(dictType.getDictType());
            }
        }
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        SysDictData dictData = new SysDictData();
        dictData.setStatus(UserConstants.NORMAL/*"0"*/);
        Map<String, List<SysDictData>> dictDataMap = dictDataService.selectSysDictDataList(dictData).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
        DictUtils.clearDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public boolean insertDictType(SysDictType dictType) {
        boolean save = save(dictType);
        if (save) {
            DictUtils.setDictCache(dictType.getDictType(), null);
        }
        return save;
    }

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateDictDataType(String oldDictType, String newDictType) {
        LambdaUpdateWrapper<SysDictType> query = Wrappers.lambdaUpdate(SysDictType.class);
        query.set(SysDictType::getDictType, newDictType).eq(SysDictType::getDictType, oldDictType);
        return update(query);
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateDictType(SysDictType dict) {
        SysDictType oldDict = getById(dict.getDictId());
        updateDictDataType(oldDict.getDictType(), dict.getDictType());
        boolean update = updateById(dict);
        if (update) {
            List<SysDictData> dictDatas = dictDataService.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return update;
    }
}
