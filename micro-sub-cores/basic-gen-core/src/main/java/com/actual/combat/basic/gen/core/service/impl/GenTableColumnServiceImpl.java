package com.actual.combat.basic.gen.core.service.impl;

import com.actual.combat.gen.domain.GenTableColumn;
import com.actual.combat.gen.mapper.GenTableColumnMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.gen.core.service.GenTableColumnService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author minimalism
 * @Date 2024/10/29 上午9:54:32
 * @Description
 */
@Service
public class GenTableColumnServiceImpl extends ServiceImpl<GenTableColumnMapper, GenTableColumn> implements GenTableColumnService{

    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId) {
        return baseMapper.selectGenTableColumnListByTableId(tableId);
    }

    @Override
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName) {
        return baseMapper.selectDbTableColumnsByName(tableName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGenTableColumnByTableIds(List<Long> tableIds) {
        return remove(Wrappers.lambdaQuery(GenTableColumn.class).in(GenTableColumn::getTableId, tableIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<GenTableColumn> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<GenTableColumn> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<GenTableColumn> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<GenTableColumn> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(GenTableColumn record) {
        return baseMapper.insertOrUpdate(record);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(GenTableColumn record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
