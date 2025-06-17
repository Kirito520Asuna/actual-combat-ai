package com.actual.combat.basic.core.config.gen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 代码生成相关配置
 *
 * @author yan
 */
@Component
@ConfigurationProperties(prefix = "config.gen.prop")
public class GenConfig {
    /**
     * 作者
     */
    public static String author;

    /**
     * 生成包路径
     */
    public static String packageName = "com.actual.combat";

    /**
     * 自动去除表前缀，默认是false
     */
    public static boolean autoRemovePre;

    /**
     * 表前缀(类名不会包含表前缀)
     */
    public static String tablePrefix;
    public static String genPrefix = "gen";

    public static String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        GenConfig.author = author;
    }

    public static String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        GenConfig.packageName = packageName;
    }

    public static boolean getAutoRemovePre() {
        return autoRemovePre;
    }

    public void setAutoRemovePre(boolean autoRemovePre) {
        GenConfig.autoRemovePre = autoRemovePre;
    }

    public static String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        GenConfig.tablePrefix = tablePrefix;
    }

    public static String getGenPrefix() {
        return genPrefix;
    }

    public static void setGenPrefix(String genPrefix) {
        GenConfig.genPrefix = genPrefix;
    }
}
