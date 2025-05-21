package com.actual_combat.aop.utils.tree;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.actual_combat.aop.all.tree.TreeMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author minimalism
 * @Date 2024/2/27 0027 17:44
 * @Description 通用树状结构
 */
public class TreeUtils {
    //        idName parentIdName subsetName 后期使用注解实现 (已实现)
    public static final String id = "id";
    public static final String parentId = "parentId";
    public static final String subset = "subset";

    /**
     * @param maps
     * @param <T>
     * @return
     */
    public static <T> List<Map<String, Object>> listToMapList(List<T> maps) {
        return maps.stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
    }

    /**
     * @param maps
     * @param beanClass
     * @param root      -- 从根目录起始 集合存在 parentId == null 时 true
     * @param map
     * @param <T>
     * @return
     */
    public static <T> List<T> mapsToTree(List<Map<String, Object>> maps, Class<T> beanClass, boolean root, Map<String, String> map) {
        String idName = ObjectUtil.defaultIfEmpty(map.get(TreeUtils.id), TreeUtils.id);
        String parentIdName = ObjectUtil.defaultIfEmpty(map.get(TreeUtils.parentId), TreeUtils.parentId);
        String subsetName = ObjectUtil.defaultIfEmpty(map.get(TreeUtils.subset), TreeUtils.subset);
        return mapsToTree(maps, beanClass, root, idName, parentIdName, subsetName);
    }


    /**
     * @param maps
     * @param beanClass
     * @param idName
     * @param parentIdName
     * @param subsetName
     * @param <T>
     * @return
     */
    public static <T> List<T> mapsToTree(List<Map<String, Object>> maps
            , Class<T> beanClass, boolean root, String idName, String parentIdName, String subsetName) {
        List<Map<String, Object>> mapList = buildTree(maps, root, idName, parentIdName, subsetName);
        return convertToBeanTree(mapList, beanClass, idName, parentIdName, subsetName);
    }

    /**
     * @param maps
     * @param idName       主键名
     * @param parentIdName 父ID名
     * @param subsetName   子集名
     * @return
     */
    public static List<Map<String, Object>> buildTree(List<Map<String, Object>> maps, boolean root
            , String idName, String parentIdName, String subsetName) {
        // 使用Map存储每个对象，便于快速查找父节点
        Map<Object, Map<String, Object>> map = new HashMap<>();
        // 存储根节点的列表
        List<Map<String, Object>> roots = new ArrayList<>();

        // 将对象存储到Map中
        for (Map<String, Object> oneMap : maps) {
            map.put(oneMap.get(idName), oneMap);
        }

        // 遍历所有对象，构建树状结构
        for (Map<String, Object> mapTwo : maps) {
            Object parentId = mapTwo.get(parentIdName);
            if (parentId != null) {
                // 如果当前对象有父节点，则将其添加到父节点的子集列表中
                Map<String, Object> parent = map.get(parentId);
                if (parent != null) {
                    List<Object> o = (List) parent.get(subsetName);
                    o.add(mapTwo);
                } else if (!root) {
                    // 如果当前对象有父节点但不是最父级，则将其视为根节点
                    roots.add(mapTwo);
                }
            } else {
                // 如果当前对象没有父节点，则将其视为根节点
                roots.add(mapTwo);
            }
        }
        return roots;
    }

    public static <T> List<T> convertToBeanTree(List<Map<String, Object>> maps, Class<T> beanClass, String idName, String parentIdName, String subsetName) {
        Map<Object, T> nodeMap = new HashMap<>();
        List<T> roots = new ArrayList<>();

        // 构建节点映射
        for (Map<String, Object> map : maps) {
            T node = BeanUtil.mapToBean(map, beanClass, true);
            nodeMap.put(map.get(idName), node);
        }

        // 构建树结构
        for (Map<String, Object> map : maps) {
            Object parentId = map.get(parentIdName);
            T currentNode = nodeMap.get(map.get(idName));
            T parentNode = nodeMap.get(parentId);
            if (parentId == null || parentNode == null) {
                roots.add(currentNode);
            } else {
                List<T> children = (List<T>) BeanUtil.getFieldValue(parentNode, subsetName);
                if (children == null) {
                    children = new ArrayList<>();
                    BeanUtil.setProperty(parentNode, subsetName, children);
                }
                children.add(currentNode);
            }
        }
        return roots;
    }

    /**
     *  将list转换为树状结构 @See {@link com.actual_combat.aop.all.tree.Tree}
     * @param list
     * @param tClass
     * @return
     * @param <T>
     */
    public static <T> List<T> listToTree(List<T> list,Class<T> tClass){
        List<Map<String, Object>> listToMapList = listToMapList(list);
        List<T> treeVos = TreeUtils.mapsToTree(listToMapList, tClass, true, TreeMap.getTreeFieldMaps(tClass));
        return treeVos;
    }
}




