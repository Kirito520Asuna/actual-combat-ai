package com.actual_combat.sharding_sphere.abs.shardingAlgorithm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Range;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * @Author yan
 * @Date 2024/12/6 23:29:43
 * @Description
 */
public interface AbsShardingAlgorithm {
    interface ConstantShardingAlgorithm {
        String SPOT = ".";
        String PREFIX = "_";
        String SHARDING_ALGORITHMS_PATH = "spring.shardingsphere.rules.sharding.sharding-algorithms";
        String COLUMN_KEY = "column";
        String TABLE_INLINE_CUSTOM_KEY = "table-inline-custom";
        String CUSTOM_ALGORITHM_EXPRESSION_KEY = "custom-algorithm-expression";
        String ALGORITHM_EXPRESSION_KEY = "algorithm-expression";
        String PROPS_KEY = "props";
        String PROPS_PATH = SHARDING_ALGORITHMS_PATH + SPOT + TABLE_INLINE_CUSTOM_KEY + SPOT + PROPS_KEY;
        String CUSTOM_ALGORITHM_EXPRESSION_PATH = PROPS_PATH + SPOT + CUSTOM_ALGORITHM_EXPRESSION_KEY;
        String ALGORITHM_EXPRESSION_PATH = PROPS_PATH + SPOT + ALGORITHM_EXPRESSION_KEY;
        String TYPE = "CUSTOM_COMPLEX";
        String COLUMN_ID_VALUE = "id";

        static String replaceTableName(String logicTableName, String key) {
            if (key.startsWith(logicTableName)) {
                int index = key.indexOf(logicTableName);
                key = key.substring(index + logicTableName.length());
            }

            String keyStr = "$";
            if (key.contains(keyStr)) {
                int index = key.indexOf(keyStr);
                key = key.substring(0, index);
            }
            return key;
        }

        static String replaceReal(String expression) {
            String prefixStr = "{";
            String suffixStr = "}";
            if (expression.startsWith(prefixStr) && expression.endsWith(suffixStr)) {
                // 找到第一个 { 和 } 的位置
                int start = expression.indexOf(prefixStr);
                int end = expression.lastIndexOf(suffixStr);
                expression = expression.substring(start + 1, end);
            }
            return expression;
        }
    }

    default String shardingSuffix(String logicTableName, Long idValue) {
        return shardingSuffix(logicTableName, (Object) idValue);
    }

    /**
     * 精确路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        精确的值
     * @return 结果表
     */
    default String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        Long idValue = shardingValue.getValue();
        // 根据精确值获取路由表
        String logicTableName = shardingValue.getLogicTableName();
        String actuallyTableName = logicTableName + shardingSuffix(logicTableName, idValue);
        if (availableTargetNames.contains(actuallyTableName)) {
            return actuallyTableName;
        }
        return null;
    }


    /**
     * 范围路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        值范围
     * @return 路由后的结果表
     */
    default Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        // 获取到范围查找的最小值，如果条件中没有最小值设置为 tableLowerDate
        Range<Long> valueRange = shardingValue.getValueRange();
        Long rangeLowerValue = valueRange.hasLowerBound() ? valueRange.lowerEndpoint() : 0l;
        Long rangeUpperValue = valueRange.hasUpperBound() ? valueRange.upperEndpoint() : Long.MAX_VALUE;

        // 根据范围值获取路由表
        List<String> tableNames = new ArrayList<>();
        // 过滤那些存在的表
        String logicTableName = shardingValue.getLogicTableName();

        while (rangeLowerValue > rangeUpperValue) {
            String actuallyTableName = logicTableName + shardingSuffix(logicTableName, rangeLowerValue);
            if (availableTargetNames.contains(actuallyTableName)) {
                tableNames.add(actuallyTableName);
            }
            rangeLowerValue++;
        }
        return tableNames;
    }

    /**
     * 泛型 ============
     *
     * @param logicTableName
     * @param idValue
     * @param <T>
     * @return
     */

    @SneakyThrows
    default <T> String shardingSuffix(String logicTableName, T idValue) {
        String expression = StrUtil.EMPTY;
        String prefix = ConstantShardingAlgorithm.PREFIX;
        Environment env = SpringUtil.getBean(Environment.class);
        String spot = ConstantShardingAlgorithm.SPOT;
        String column = env.getProperty(ConstantShardingAlgorithm.PROPS_PATH + spot + ConstantShardingAlgorithm.COLUMN_KEY);
        column = StrUtil.isBlank(column) ? ConstantShardingAlgorithm.COLUMN_ID_VALUE : column;
        expression = env.getProperty(ConstantShardingAlgorithm.PROPS_PATH + spot + ConstantShardingAlgorithm.CUSTOM_ALGORITHM_EXPRESSION_KEY);

        if (StrUtil.isBlank(expression)) {
            String algorithmExpression = env.getProperty(ConstantShardingAlgorithm.PROPS_PATH + spot + ConstantShardingAlgorithm.ALGORITHM_EXPRESSION_KEY);
            if (StrUtil.isBlank(algorithmExpression)) {
                throw new Exception(ConstantShardingAlgorithm.ALGORITHM_EXPRESSION_KEY + " is not null");
            }
            String[] split = algorithmExpression.split("->");
            String prefixTable = split[0];
            prefix = ConstantShardingAlgorithm.replaceTableName(logicTableName, prefixTable);
            expression = ConstantShardingAlgorithm.replaceReal(split[split.length - 1]);
        }

        if (StrUtil.isBlank(expression)) {
            throw new Exception("expression is null");
        }

        expression = ConstantShardingAlgorithm.replaceTableName(logicTableName, expression);
        expression = ConstantShardingAlgorithm.replaceReal(expression);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(column, idValue);

        String execute = "";
        try {
            Object executeReturn = AviatorEvaluator.execute(expression, map);
            execute = prefix + executeReturn;
        } catch (Exception e) {
            throw new Exception("分片算法执行失败");
        }
        return execute;
    }

    /**
     * 精确路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        精确的值
     * @return 结果表
     */
    default <T extends Comparable<?>> String doShardingOne(Collection<String> availableTargetNames, PreciseShardingValue<T> shardingValue) {
        T idValue = shardingValue.getValue();
        // 根据精确值获取路由表
        String logicTableName = shardingValue.getLogicTableName();
        String actuallyTableName = logicTableName + shardingSuffix(logicTableName, idValue);
        if (availableTargetNames.contains(actuallyTableName)) {
            return actuallyTableName;
        }
        return null;
    }

    /**
     * 范围路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        值范围
     * @return 路由后的结果表
     */
    default <T extends Comparable<?>> Collection<String> doShardingList(Collection<String> availableTargetNames, RangeShardingValue<T> shardingValue) {
        // 获取到范围查找的最小值，如果条件中没有最小值设置为 tableLowerDate
        Range<T> valueRange = shardingValue.getValueRange();
        Long rangeLowerValue = Long.parseLong(String.valueOf(valueRange.hasLowerBound() ? valueRange.lowerEndpoint() : 0l));
        Long rangeUpperValue = Long.parseLong(String.valueOf(valueRange.hasUpperBound() ? valueRange.upperEndpoint() : Long.MAX_VALUE));

        // 根据范围值获取路由表
        List<String> tableNames = new ArrayList<>();
        // 过滤那些存在的表
        String logicTableName = shardingValue.getLogicTableName();

        while (rangeLowerValue > rangeUpperValue) {
            String actuallyTableName = logicTableName + shardingSuffix(logicTableName, (T) rangeLowerValue);
            if (availableTargetNames.contains(actuallyTableName)) {
                tableNames.add(actuallyTableName);
            }
            rangeLowerValue++;
        }
        return tableNames;
    }


}
