package com.actual_combat.jpa.utils;


import cn.hutool.core.collection.CollUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2024/6/6 0006 13:47
 * @Description
 */
public class JpaPageUtils {
    enum Type {
        sql, page, count
    }

    public final static String template = "select * from (%s) as %s";
    public final static String countTemplate = "select count(*) from (%s) as %s ;";
    public final static String templatePage = template + " limit %s,%s ;";

    /**
     * 获取分页SQLCount
     *
     * @param sql
     * @return
     */
    public static String getCountSql(@Validated @NotBlank String sql) {
        String substring = UUID.randomUUID().toString().substring(0, 4);
        return String.format(countTemplate, sql, "count_" + substring);
    }

    /**
     * 获取分页SQL
     *
     * @param sql
     * @param pageNum
     * @param pageSize
     * @return
     */
    public static String getPageSql(@Validated @NotBlank String sql, int pageNum, int pageSize) {
        String substring = UUID.randomUUID().toString().substring(0, 4);
        return String.format(templatePage, sql,"page_"+ substring, (pageNum - 1) * pageSize, pageSize);
    }

    public static String getSql(@Validated @NotBlank String sql) {
        String substring = UUID.randomUUID().toString().substring(0, 4);
        return String.format(template, sql, "sql_" + substring);
    }

    /**
     * 转换List为Page
     *
     * @param list
     * @param pageable
     * @param total
     * @param <T>
     * @return
     */
    public static <T> Page<T> listToPage(List<T> list, @Validated @NotNull Pageable pageable, long total) {
        Page<T> tPage = new PageImpl<>(list, pageable, total);
        return tPage;
    }

    /**
     * 转换List为Page
     *
     * @param list
     * @param pageNum
     * @param pageSize
     * @param total
     * @param <T>
     * @return
     */
    public static <T> Page<T> listToPage(List<T> list, int pageNum, int pageSize, long total) {
        PageRequest of = PageRequest.of(pageNum, pageSize);
        return listToPage(list, of, total);
    }

    /**
     * 转换Page为Page<T>
     *
     * @param page
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Page<T> pageToPage(@Validated @NotNull Page page, List<T> list) {
        Page<T> tPage = listToPage(list, page.getPageable(), page.getTotalElements());
        return tPage;
    }

    /**
     * 获取查询 |sql|page|count|
     *
     * @param entityManager -- 实体管理器
     * 获取方式：dao 层 实现 加入 该代码
     *     private EntityManager entityManager;
     *
     *     @PersistenceContext
     *     public void setEntityManager(EntityManager entityManager) {
     *         this.entityManager = entityManager;
     *     }
     *
     * @param sql
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static Query getQuery(@Validated @NotNull EntityManager entityManager, @NotBlank String sql, @NotNull Type type, Integer pageNo, Integer pageSize) {
        String rsql;
        if (type == Type.count) {
            rsql = getCountSql(sql);
        } else {
            rsql = getSql(sql);
            rsql = sql;
        }
        Query nativeQuery = entityManager.createNativeQuery(rsql);
        if (type == Type.page) {
            nativeQuery.setFirstResult((pageNo - 1) * pageSize);
            nativeQuery.setMaxResults(pageSize);
        }
        return nativeQuery;
    }
    public static Query getQuery(@Validated @NotNull EntityManager entityManager, @NotBlank String sql, @NotNull Type type, Integer pageNo, Integer pageSize, Map<String, Object> parameterMap) {
        String rsql;
        if (type == Type.count) {
            rsql = getCountSql(sql);
        } else {
            rsql = getSql(sql);
            rsql = sql;
        }
        Query nativeQuery = entityManager.createNativeQuery(rsql);
        if (type == Type.page) {
            nativeQuery.setFirstResult((pageNo - 1) * pageSize);
            nativeQuery.setMaxResults(pageSize);
        }

        if (CollUtil.isNotEmpty(parameterMap)) {
            for (String key : parameterMap.keySet()) {
                nativeQuery.setParameter(key, parameterMap.get(key));
            }
        }
        return nativeQuery;
    }


    public static <T> void setResultTransformer(@Validated @NotNull Query query, @NotNull Class<T> tClass) {
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(tClass));
    }

    /**
     * @param query ==>  PageUtils.getQuery(entityManager, sql, Type.page, pageNo, pageSize);
     * @param countQuery ==>  PageUtils.getQuery(entityManager, sql, Type.count, null, null);
     * @param tClass
     * @param pageNo
     * @param pageSize
     * @param <T>
     * @return
     */
    public static <T> Page<T> getPage(@Validated @NotNull Query query, @NotNull Query countQuery, @NotNull Class<T> tClass, @NotNull Integer pageNo, @NotNull Integer pageSize) {
        //query.setMaxResults(pageSize);
        //query.setFirstResult((pageNo - 1) * pageSize);
        //query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(tClass));
        setResultTransformer(query, tClass);
        List<T> list = query.getResultList();

        PageRequest pageable = PageRequest.of((pageNo - 1), pageSize);
        Page<T> page = new PageImpl<>(new ArrayList<>(), pageable, 0);
        if (CollUtil.isNotEmpty(list)) {
            List resultList = countQuery.getResultList();
            long count = Long.parseLong(resultList.get(0).toString());
            page = new PageImpl(list, pageable, count);
        }
        return page;
    }

    /**
     *
     * @param query
     * @param tClass
     * @return
     * @param <T>
     */

    public static <T> List<T> getList(@Validated @NotNull Query query,  @NotNull Class<T> tClass) {
        setResultTransformer(query, tClass);
        List<T> list = query.getResultList();
        return list;
    }

}
