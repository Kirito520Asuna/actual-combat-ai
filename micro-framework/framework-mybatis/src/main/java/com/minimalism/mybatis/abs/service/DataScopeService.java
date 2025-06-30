package com.minimalism.mybatis.abs.service;


import com.minimalism.mybatis.aop.constants.DataScopeConstants;
import com.minimalism.mybatis.aop.domain.DataScopeAboutTable;

import java.util.Map;

public interface DataScopeService extends DataScopeConstants {
    /**
     * 获取数据权限sql模板
     * @return
     */
    default Map<String, String> fetchDataScopeSqlBuildFormatMap() {
        return null;
    }

    /**
     * 获取DataScopeAboutTable
     */
    default DataScopeAboutTable fetchDataScopeAboutTable() {
        return new DataScopeAboutTable();
    }
}
