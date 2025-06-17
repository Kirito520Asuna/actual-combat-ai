package com.actual.combat.basic.utils.str;

import cn.hutool.core.util.StrUtil;

public class StrUtils extends StrUtil {
    /**
     * 去除字符串中的空格
     *
     * @param str 输入的字符串
     * @return 去除空格后的字符串
     */
    public static String trim(String str) {
        // 创建一个StringBuilder对象，用于构建新的字符串
        StringBuilder sb = new StringBuilder(str);
        // 初始化索引变量，用于记录空格的位置
        int index = 0;
        // 使用while循环查找并删除字符串中的所有空格
        // sb.indexOf(" ") 返回字符串中第一个空格的位置，如果没有空格则返回-1
        while ((index = sb.indexOf(" ")) != -1) {
            // 使用deleteCharAt方法删除指定位置的字符（空格）
            sb.deleteCharAt(index);
        }
        // 将StringBuilder对象转换为字符串并返回
        return sb.toString();
    }

    public static boolean isHttp(String path) {
        return StrUtil.startWith(path, "http") || StrUtil.startWith(path, "https");
    }
}
