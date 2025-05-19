package com.actual_combat.sharding_sphere.shardingAlgorithm;

import cn.hutool.core.util.StrUtil;
import com.actual_combat.sharding_sphere.abs.shardingAlgorithm.AbsShardingAlgorithm;
import com.googlecode.aviator.AviatorEvaluator;
import groovy.util.logging.Slf4j;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author yan
 * @Date 2024/12/5 20:54:54
 * @Description //自定义分片策略
 */
@Slf4j
public class IdCustomShardingAlgorithm implements AbsShardingAlgorithm, StandardShardingAlgorithm<Long> {
    private Properties props = new Properties();

    /**
     * 返回算法类型，用于标识分片算法。
     *
     * @return 算法类型字符串
     */
    @Override
    public String getType() {
        return ConstantShardingAlgorithm.TYPE; // 自定义算法类型
    }

    /**
     * 初始化方法，可用于加载配置（如果需要）。
     */
    @Override
    public void init() {
    }


    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void setProps(Properties properties) {
        this.props = properties;
    }

    @SneakyThrows
    @Override
    public String shardingSuffix(String logicTableName, Long idValue) {
        String expression = StrUtil.EMPTY;
        String prefix = ConstantShardingAlgorithm.PREFIX;

        String column = props.getProperty(ConstantShardingAlgorithm.COLUMN_KEY);
        column = StrUtil.isBlank(column) ? ConstantShardingAlgorithm.COLUMN_ID_VALUE : column;
        expression = props.getProperty(ConstantShardingAlgorithm.CUSTOM_ALGORITHM_EXPRESSION_KEY);

        if (StrUtil.isBlank(expression)) {
            String algorithmExpression = props.getProperty(ConstantShardingAlgorithm.ALGORITHM_EXPRESSION_KEY);
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
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        return AbsShardingAlgorithm.super.doSharding(availableTargetNames, shardingValue);
    }


    /**
     * 范围路由算法
     *
     * @param availableTargetNames 可用的表列表（配置文件中配置的 actual-data-nodes会被解析成 列表被传递过来）
     * @param shardingValue        值范围
     * @return 路由后的结果表
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        return AbsShardingAlgorithm.super.doSharding(availableTargetNames, shardingValue);
    }


}
