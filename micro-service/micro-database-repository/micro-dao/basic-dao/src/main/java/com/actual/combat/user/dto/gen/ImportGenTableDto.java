package com.actual.combat.user.dto.gen;

import com.actual.combat.basic.abs.service.AbsValidate;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.utils.object.ObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportGenTableDto implements Serializable, AbsValidate<ImportGenTableDto> {
    @Schema(description = "表名 ，多个用逗号隔开")
    String tables;
    @Schema(description = "模块名 用于拼接权限前缀 以gen为例 如：${moduleName}:gen:list,${moduleName}:gen:add ...")
    String modelName;

    @Override
    public boolean validateOk(ImportGenTableDto importGenTableDto) {
        if (ObjectUtils.isEmpty(importGenTableDto.getTables())) {
            throw new BusinessException("表名不能为空");
        }
        return true;
    }
}
