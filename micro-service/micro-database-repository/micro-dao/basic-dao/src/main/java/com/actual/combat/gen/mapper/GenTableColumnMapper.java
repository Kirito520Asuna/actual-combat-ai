package com.actual.combat.gen.mapper;

import com.actual.combat.gen.domain.GenTableColumn;
import com.actual.combat.mp.abs.mapper.MpMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/29 上午9:55:25
 * @Description
 */
@Mapper
public interface GenTableColumnMapper extends MpMapper<GenTableColumn> {
    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    List<GenTableColumn> selectGenTableColumnListByTableId(@Param("tableId") Long tableId);
    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName);

    int updateBatch(@Param("list") List<GenTableColumn> list);

    int updateBatchSelective(@Param("list") List<GenTableColumn> list);

    int batchInsert(@Param("list") List<GenTableColumn> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<GenTableColumn> list);

    int deleteByPrimaryKeyIn(@Param("list") List<Long> list);

    int insertOrUpdate(GenTableColumn record);

    int insertOrUpdateSelective(GenTableColumn record);

}