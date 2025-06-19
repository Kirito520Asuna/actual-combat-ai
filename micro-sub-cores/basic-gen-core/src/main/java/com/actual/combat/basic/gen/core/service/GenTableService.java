package com.actual.combat.basic.gen.core.service;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.actual.combat.gen.domain.GenTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2024/10/29 上午9:54:32
 * @Description
 */
public interface GenTableService extends IService<GenTable> {
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
    List<GenTable> selectDbTableListByNames(List<String> tableNames);

    /**
     * 查询表名列表
     *
     * @return
     */
    List<String> selectGenTableNameList();

    /**
     * 删除业务信息
     *
     * @param tableIds 需要删除的表数据ID
     * @return 结果
     */
    boolean deleteGenTableByIds(List<Long> tableIds);

    /**
     * 生成代码（下载方式）
     *
     * @param tableName  表名称
     * @param moduleName
     * @return 数据
     */
    byte[] downloadCode(String tableName, String usePermissions, String moduleName);

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableNames     表数组
     * @param usePermissions
     * @return 数据
     */
    byte[] downloadCode(List<String> tableNames, String usePermissions, String moduleName);

    /**
     * 同步数据库
     *
     * @param tableName 表名称
     */
    void synchDb(String tableName);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName      表名称
     * @param usePermissions
     * @return 数据
     */
    void generatorCode(String tableName, String usePermissions);


    /**
     * 生成代码（自定义路径）
     *
     * @param tableName      表名称
     * @param usePermissions
     * @return 数据
     */
    void generatorCode(String tableName, String usePermissions, String moduleName);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName
     * @param usePermissions
     * @param zip
     */
    void generatorCode(String tableName, String usePermissions, ZipOutputStream zip);

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName
     * @param usePermissions
     * @param moduleName
     * @param zip
     */
    void generatorCode(String tableName, String usePermissions, String moduleName, ZipOutputStream zip);

    /**
     * 查询表信息
     *
     * @param tableName
     * @return
     */
    GenTable selectGenTableByName(String tableName);

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     */
    void importGenTable(List<GenTable> tableList);

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    Map<String, String> previewCode(Long tableId);

    /**
     * 修改保存参数校验
     *
     * @param genTable 业务信息
     */
    void validateEdit(GenTable genTable);

    /**
     * 修改业务
     *
     * @param genTable 业务信息
     * @return 结果
     */
    void updateGenTable(GenTable genTable);

    int updateBatch(List<GenTable> list);

    int updateBatchSelective(List<GenTable> list);

    int batchInsert(List<GenTable> list);

    int batchInsertSelectiveUseDefaultForNull(List<GenTable> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(GenTable record);

    int insertOrUpdateSelective(GenTable record);

}
