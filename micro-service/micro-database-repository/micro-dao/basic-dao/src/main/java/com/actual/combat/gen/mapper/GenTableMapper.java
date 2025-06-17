package com.actual.combat.gen.mapper;

import com.actual.combat.gen.domain.GenTable;
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
public interface GenTableMapper extends MpMapper<GenTable> {
    /**
     * 查询业务列表
     *
     * @param genTable 业务信息
     * @return 业务集合
     */
    List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询据库列表
     *
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    List<GenTable> selectDbTableList(GenTable genTable);
    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    List<GenTable> selectDbTableListByNames(@Param("tableNames") List<String> tableNames);

    int updateBatch(@Param("list") List<GenTable> list);

    int updateBatchSelective(@Param("list") List<GenTable> list);

    int batchInsert(@Param("list") List<GenTable> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<GenTable> list);

    int deleteByPrimaryKeyIn(@Param("list") List<Long> list);

    int insertOrUpdate(GenTable record);

    int insertOrUpdateSelective(GenTable record);

}