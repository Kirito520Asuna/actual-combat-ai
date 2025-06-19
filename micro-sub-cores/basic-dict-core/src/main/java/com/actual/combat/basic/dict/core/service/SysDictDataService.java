package com.actual.combat.basic.dict.core.service;

import java.util.List;

import com.actual.combat.dict.domain.SysDictData;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysDictDataService extends IService<SysDictData>{


    int updateBatch(List<SysDictData> list);

    int updateBatchUseMultiQuery(List<SysDictData> list);

    int updateBatchSelective(List<SysDictData> list);

    int batchInsert(List<SysDictData> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysDictData> list);

    int insertOnDuplicateUpdate(SysDictData record);

    int insertOnDuplicateUpdateSelective(SysDictData record);

    List<SysDictData> selectSysDictDataList(SysDictData sysDictData);
    /**
     * 批量删除字典数据信息
     *
     * @param dictCodeList 需要删除的字典数据ID
     */
    void deleteByDictCodes(List<Long> dictCodeList);
    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictData> selectDictDataByType(String dictType);
}
