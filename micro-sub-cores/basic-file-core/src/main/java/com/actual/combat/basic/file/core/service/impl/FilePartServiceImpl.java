package com.actual.combat.basic.file.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.file.domain.FilePart;
import com.actual.combat.file.mapper.FilePartMapper;
import com.actual.combat.vo.PartVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.actual.combat.basic.file.core.service.FilePartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author yan
 * @Date 2025/3/6 14:16:20
 * @Description
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FilePartServiceImpl extends ServiceImpl<FilePartMapper, FilePart> implements FilePartService {

    @Override
    public int updateBatch(List<FilePart> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchUseMultiQuery(List<FilePart> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    public int updateBatchSelective(List<FilePart> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<FilePart> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<FilePart> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    public int batchInsertOrUpdate(List<FilePart> list) {
        return baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }

    @Override
    public int insertOnDuplicateUpdate(FilePart record) {
        return baseMapper.insertOnDuplicateUpdate(record);
    }

    @Override
    public int insertOnDuplicateUpdateSelective(FilePart record) {
        return baseMapper.insertOnDuplicateUpdateSelective(record);
    }

    @Override
    public List<PartVo> getPartList(String identifier, Long fileId) {
        return baseMapper.getPartList(identifier, fileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removePart(String identifier, Long fileId) {
        //分片不存储至云端 ，直接删除本地文件
        List<PartVo> partList = getPartList(identifier, fileId);
        List<PartVo> localList = CollUtil.newArrayList();
        List<PartVo> urlList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(partList)) {
            partList.stream().forEach(part -> {
                if (part.getLocal()) {
                    localList.add(part);
                } else {
                    urlList.add(part);
                }
            });
        }
        //删除云端文件
        // todo:
        //删除本地文件
        localList.stream().map(PartVo::getLocalResource).forEach(FileUtil::del);
        //删除数据库记录
        return baseMapper.deleteByFileId(identifier, fileId);
    }

    @Override
    public Long getOneFileIdByCode(String identifier) {
        PageHelper.startPage(1, 1);
        LambdaQueryWrapper<FilePart> wrapper = Wrappers.lambdaQuery(FilePart.class).select(FilePart::getFileId)
                .eq(FilePart::getPartCode, identifier).eq(FilePart::getMergeDelete,Boolean.FALSE);
        return Optional.ofNullable(getOne(wrapper)).orElseGet(FilePart::new).getFileId();
    }

    @Override
    public FilePart getByCode(String identifier,int sort) {
        LambdaQueryWrapper<FilePart> wrapper = Wrappers.lambdaQuery(FilePart.class)
                .eq(FilePart::getPartCode, identifier)
                .eq(FilePart::getPartSort, sort);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByEntityFileId(FilePart filePart) {
        String identifier = filePart.getPartCode();
        Long fileId = filePart.getFileId();

        LambdaUpdateWrapper<FilePart> wrapper = Wrappers.lambdaUpdate(FilePart.class);
        wrapper.set(FilePart::getMergeDelete, filePart.getMergeDelete())
                .eq(StrUtil.isNotBlank(identifier), FilePart::getPartCode, identifier)
                .eq(FilePart::getFileId, fileId);
        return update(wrapper);
    }

    @Override
    public int getCountByFileId(Long fileId, String identifier) {
        if (ObjectUtils.isEmpty(fileId) && StrUtil.isBlank(identifier)) {
            return 0;
        }
        LambdaQueryWrapper<FilePart> wrapper = Wrappers.lambdaQuery(FilePart.class);
        wrapper.select(FilePart::getPartId)
                .eq(ObjectUtils.isNotEmpty(fileId),FilePart::getFileId, fileId)
                .eq(StrUtil.isNotBlank(identifier),FilePart::getPartCode, identifier)
                .groupBy(FilePart::getPartCode);
        return (int) count(wrapper);
    }

    @Override
    public List<FilePart> getPartsByFileIdFirstPartCount(Long fileId, int partCount, List<Long> excludeIds) {
        return baseMapper.getPartsByFileIdFirstPartCount(fileId, partCount,excludeIds);
    }
}
