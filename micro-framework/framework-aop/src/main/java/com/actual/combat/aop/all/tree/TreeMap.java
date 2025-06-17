package com.actual.combat.aop.all.tree;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yan
 * @date 2024/2/29 1:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeMap {
    /**
     * id 名称
     *
     * @return
     */
    String id;

    /**
     * 父id 名称
     *
     * @return
     */
    String parentId;

    /**
     * 子集 名称
     *
     * @return
     */
    String subset;


    public static Map<String, String> getTreeFieldMaps(Class<?> tClass) {
        Map<String, String> map = new LinkedHashMap<>();
        Field[] fields = tClass.getDeclaredFields(); // 获取Demo类中所有定义的字段
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Tree.class); // 获取当前字段上的Tree注解
            if (annotation instanceof Tree) { // 判断当前字段是否有Treed注解
                Tree tree = (Tree) annotation; // 将注解强制转换为Tree类型
                String nameValue = field.getName();

                boolean id = tree.id();
                boolean parentId = tree.parentId();
                boolean subset = tree.subset();
                if (id) {
                    map.put("id", nameValue);
                } else if (parentId) {
                    map.put("parentId", nameValue);
                } else if (subset) {
                    map.put("subset", nameValue);
                }
            }
        }
        return map;
    }


    public static TreeMap getTreeMap(Class<?> tClass) {
        Map<String, String> map = getTreeFieldMaps(tClass);
        TreeMap treeMap = BeanUtil.mapToBean(map, TreeMap.class, true);
        return treeMap;
    }


}
