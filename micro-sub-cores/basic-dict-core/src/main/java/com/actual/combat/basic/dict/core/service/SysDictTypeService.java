package com.actual.combat.basic.dict.core.service;

import java.util.List;

import com.actual.combat.dict.domain.SysDictType;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysDictTypeService extends IService<SysDictType>{


    int updateBatch(List<SysDictType> list);

    int updateBatchUseMultiQuery(List<SysDictType> list);

    int updateBatchSelective(List<SysDictType> list);

    int batchInsert(List<SysDictType> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDictType> list);

    int insertOnDuplicateUpdate(SysDictType record);

    int insertOnDuplicateUpdateSelective(SysDictType record);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    boolean checkDictTypeUnique(SysDictType dictType);
    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     *
     */
    List<SysDictType> selectSysDictTypeList(SysDictType dictType);

    /**
     * 批量删除字典信息
     *
     * @param dictIds 需要删除的字典ID
     */
    void deleteByDictIds(List<Long> dictIds);

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();

    /**
     * 加载字典缓存数据
     */
    void loadingDictCache();

    /**
     * 清空字典缓存数据
     */
    void clearDictCache();

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    boolean insertDictType(SysDictType dictType);

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    boolean updateDictDataType(String oldDictType, String newDictType);

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    boolean updateDictType(SysDictType dict);
}
