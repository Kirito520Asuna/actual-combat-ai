package com.actual.combat.basic.gen.core.service;

import java.util.List;

import com.actual.combat.gen.domain.GenTableColumn;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2024/10/29 上午9:54:32
 * @Description
 */
public interface GenTableColumnService extends IService<GenTableColumn> {
    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);
    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName);

    /**
     * 批量删除业务字段
     *
     * @param tableIds 需要删除的数据ID
     * @return 结果
     */
    boolean deleteGenTableColumnByTableIds(List<Long> tableIds);

    int updateBatch(List<GenTableColumn> list);

    int updateBatchSelective(List<GenTableColumn> list);

    int batchInsert(List<GenTableColumn> list);

    int batchInsertSelectiveUseDefaultForNull(List<GenTableColumn> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(GenTableColumn record);

    int insertOrUpdateSelective(GenTableColumn record);

}
