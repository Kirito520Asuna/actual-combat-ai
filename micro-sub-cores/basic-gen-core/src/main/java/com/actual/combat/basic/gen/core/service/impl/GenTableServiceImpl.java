package com.actual.combat.basic.gen.core.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.basic.constant.Constants;
import com.actual.combat.basic.core.config.gen.GenConfig;
import com.actual.combat.basic.core.constant.gen.GenConstants;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.utils.other.StringUtils;
import com.actual.combat.basic.utils.other.text.CharsetKit;
import com.actual.combat.basic.utils.str.StrUtils;
import com.actual.combat.gen.domain.GenTable;
import com.actual.combat.gen.domain.GenTableColumn;
import com.actual.combat.gen.mapper.GenTableMapper;
import com.actual.combat.gen.utli.GenUtils;
import com.actual.combat.gen.utli.VelocityInitializer;
import com.actual.combat.gen.utli.VelocityUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.actual.combat.basic.gen.core.service.GenTableColumnService;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.gen.core.service.GenTableService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Author minimalism
 * @Date 2024/10/29 上午9:54:32
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableService {
    @Resource
    private GenTableColumnService genTableColumnService;

    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        return baseMapper.selectGenTableList(genTable);
    }

    @Override
    public List<String> selectGenTableNameList() {
        List<String> tables = list(Wrappers.lambdaQuery(GenTable.class).select(GenTable::getTableName))
                .stream().map(GenTable::getTableName).collect(Collectors.toList());
        return tables;
    }

    /**
     * 删除业务对象
     *
     * @param tableIds 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGenTableByIds(List<Long> tableIds) {
        boolean removeByIds = removeByIds(tableIds);
        boolean columnByTableIds = genTableColumnService.deleteGenTableColumnByTableIds(tableIds);
        return removeByIds && columnByTableIds;
    }

    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        return baseMapper.selectDbTableList(genTable);
    }

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    @Override
    public List<GenTable> selectDbTableListByNames(List<String> tableNames) {
        return baseMapper.selectDbTableListByNames(tableNames);
    }



    /**
     * 生成代码（下载方式）
     *
     * @param tableName  表名称
     * @param moduleName
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String tableName, String usePermissions, String moduleName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName,usePermissions, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableNames     表数组
     * @param usePermissions
     * @return 数据
     */
    @Override
    public byte[] downloadCode(List<String> tableNames, String usePermissions, String moduleName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            generatorCode(tableName,usePermissions,moduleName, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 同步数据库
     *
     * @param tableName 表名称
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchDb(String tableName) {
        GenTable table = selectGenTableByName(tableName);
        List<GenTableColumn> tableColumns = table.getColumns();
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream().collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));
        List<GenTableColumn> dbTableColumns = genTableColumnService.selectDbTableColumnsByName(tableName);
        if (StringUtils.isEmpty(dbTableColumns)) {
            throw new GlobalCustomException("同步数据失败，原表结构不存在");
        }
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).collect(Collectors.toList());

        dbTableColumns.forEach(column -> {
            GenUtils.initColumnField(column, table);
            if (tableColumnMap.containsKey(column.getColumnName())) {
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                column.setColumnId(prevColumn.getColumnId());
                if (column.isList()) {
                    // 如果是列表，继续保留查询方式/字典类型选项
                    column.setDictType(prevColumn.getDictType());
                    column.setQueryType(prevColumn.getQueryType());
                }
                if (StringUtils.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
                        && (column.isInsert() || column.isEdit())
                        && ((column.isUsableColumn()) || (!column.isSuperColumn()))) {
                    // 如果是(新增/修改&非主键/非忽略及父属性)，继续保留必填/显示类型选项
                    column.setIsRequired(prevColumn.getIsRequired());
                    column.setHtmlType(prevColumn.getHtmlType());
                }
                genTableColumnService.updateById(column);
            } else {
                genTableColumnService.save(column);
            }
        });

        List<Long> columnIds = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName()))
                .map(GenTableColumn::getColumnId).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(columnIds)) {
            genTableColumnService.removeByIds(columnIds);
        }
    }


    /**
     * 生成代码（自定义路径）
     *
     * @param tableName      表名称
     * @param usePermissions
     */
    @Override
    public void generatorCode(String tableName, String usePermissions) {
        generatorCode(tableName,usePermissions,"");
    }
    /**
     * 生成代码（自定义路径）
     *
     * @param tableName      表名称
     * @param usePermissions
     */
    @Override
    public void generatorCode(String tableName, String usePermissions, String moduleName) {
        // 查询表信息
        GenTable table = selectGenTableByName(tableName);
        // 设置主子表信息
        table.setSubTable(null)
                // 设置主键列信息
                .setPkColumn(null)
                .setUsePermissions(usePermissions);

        if (StrUtils.isNotBlank(moduleName)) {
            table.setModuleName(moduleName);
        }

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) {
            if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm", "index-tree.vue.vm")) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                tpl.merge(context, sw);
                try {
                    String path = getGenPath(table, template);
                    System.err.println("path:" + path);
                    FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
                } catch (IOException e) {
                    throw new GlobalCustomException("渲染模板失败，表名：" + table.getTableName());
                }
            }
        }
    }

    /**
     * 查询表信息并生成代码
     */
    @Override
    public void generatorCode(String tableName, String usePermissions, ZipOutputStream zip) {
        generatorCode(tableName,usePermissions,null, zip);
    }

    /**
     * 查询表信息并生成代码
     */
    @Override
    public void generatorCode(String tableName, String usePermissions, String moduleName, ZipOutputStream zip) {
        // 查询表信息
        GenTable table = selectGenTableByName(tableName);
        // 设置主子表信息
        table.setSubTable(null)
                // 设置主键列信息
                .setPkColumn(null)
                .setUsePermissions(usePermissions);

        if (StrUtils.isNotEmpty(moduleName)) {
            table.setModuleName(moduleName);
        }

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }


    @Override
    public GenTable selectGenTableByName(String tableName) {
        return getOne(Wrappers.lambdaQuery(GenTable.class).eq(GenTable::getTableName, tableName));
    }

    /**
     * 获取代码生成地址
     *
     * @param table    业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template) {
        String genPath = table.getGenPath();
        String path;
        if (StringUtils.equals(genPath, "/")) {
            path = System.getProperty("user.dir") + File.separator + GenConfig.getGenPrefix() + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        } else {
            path = genPath + File.separator + GenConfig.getGenPrefix() + File.separator + VelocityUtils.getFileName(template, table);
        }
        return path;
    }


    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGenTable(List<GenTable> tableList) {
        String operName = SpringUtil.getBean(AuthUserService.class).getUserId();
        try {
            for (GenTable table : tableList) {
                String tableName = table.getTableName();
                GenUtils.initTable(table, operName);
                boolean save = save(table);
                if (save) {
                    // 保存列信息
                    List<GenTableColumn> genTableColumns = genTableColumnService.selectDbTableColumnsByName(tableName);
                    for (GenTableColumn column : genTableColumns) {
                        GenUtils.initColumnField(column, table);
                        genTableColumnService.save(column);
                    }
                }
            }
        } catch (Exception e) {
            throw new GlobalCustomException("导入失败：" + e.getMessage());
        }
    }

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    @Override
    public Map<String, String> previewCode(Long tableId) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        // 查询表信息
        GenTable table = getById(tableId);
        // 设置主子表信息
        table.setSubTable(null)
                // 设置主键列信息
                .setPkColumn(null);
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    /**
     * 修改保存参数校验
     *
     * @param genTable 业务信息
     */
    @Override
    public void validateEdit(GenTable genTable) {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) {
            String options = JSON.toJSONString(genTable.getParams());
            JSONObject paramsObj = JSON.parseObject(options);
            if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_CODE))) {
                throw new GlobalCustomException("树编码字段不能为空");
            } else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) {
                throw new GlobalCustomException("树父编码字段不能为空");
            } else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_NAME))) {
                throw new GlobalCustomException("树名称字段不能为空");
            } else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) {
                if (StringUtils.isEmpty(genTable.getSubTableName())) {
                    throw new GlobalCustomException("关联子表的表名不能为空");
                } else if (StringUtils.isEmpty(genTable.getSubTableFkName())) {
                    throw new GlobalCustomException("子表关联的外键名不能为空");
                }
            }
        }
    }

    /**
     * 修改业务
     *
     * @param genTable 业务信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGenTable(GenTable genTable) {
        String options = JSON.toJSONString(genTable.getParams());
        genTable.setOptions(options);
        boolean update = updateById(genTable);
        if (update) {
            genTableColumnService.updateBatchById(genTable.getColumns());
        }
    }

    @Override
    public int updateBatch(List<GenTable> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchSelective(List<GenTable> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<GenTable> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<GenTable> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }

    @Override
    public int insertOrUpdate(GenTable record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(GenTable record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
