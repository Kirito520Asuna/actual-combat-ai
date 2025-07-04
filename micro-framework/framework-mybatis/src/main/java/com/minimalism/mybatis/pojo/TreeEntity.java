package com.minimalism.mybatis.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree基类
 *
 * @author yan
 */
@Data
public abstract class TreeEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 子部门
     */
    private List<? extends TreeEntity> children = new ArrayList<>();

}
