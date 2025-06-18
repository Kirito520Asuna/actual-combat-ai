package com.actual.combat.vo.dept;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author yan
 * @Date 2025/4/25 01:22:54
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptTreeVo implements Serializable {
    private Long id;
    private Long parentId;
    private long level;
}
