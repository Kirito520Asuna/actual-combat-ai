package com.actual.combat.basic.dict.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.dict.core.utils.DictUtils;
import com.actual.combat.dict.domain.SysDictData;
import com.actual.combat.dict.mapper.SysDictDataMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.actual.combat.basic.dict.core.service.SysDictDataService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysDictData> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchUseMultiQuery(List<SysDictData> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysDictData> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysDictData> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysDictData> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOnDuplicateUpdate(SysDictData record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOnDuplicateUpdateSelective(SysDictData record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }

    @Override
    public List<SysDictData> selectSysDictDataList(SysDictData sysDictData) {
        return baseMapper.selectSysDictDataList(sysDictData);
    }

    @Override
    public void deleteByDictCodes(List<Long> dictCodeList) {
        if (CollUtil.isNotEmpty(dictCodeList)) {
            List<SysDictData> sysDictData = listByIds(dictCodeList);
            for (SysDictData dictData : sysDictData) {
                removeById(dictData.getDictCode());
                List<SysDictData> dictDataList = baseMapper.selectDictDataListByType(dictData.getDictType());
                DictUtils.setDictCache(dictData.getDictType(), dictDataList);
            }
        }
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDataList = CollUtil.newArrayList();
        List<SysDictData> dictCache = DictUtils.getDictCache(dictType);
        if (null != dictCache) {
            dictDataList.addAll(dictCache);
        }
        if (CollUtil.isEmpty(dictDataList)) {
            List<SysDictData> dataList = baseMapper.selectDictDataListByType(dictType);
            DictUtils.setDictCache(dictType, dataList);
            dictDataList.addAll(dataList);
        }
        return dictDataList;
    }
}
