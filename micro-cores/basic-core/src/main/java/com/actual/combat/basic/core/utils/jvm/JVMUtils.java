package com.actual.combat.basic.core.utils.jvm;

/**
 * @Author yan
 * @Date 2025/3/9 20:11:26
 * @Description
 */
public class JVMUtils {
    /**
     * 获取当前 JVM 的运行时对象
     * @return
     */
    public static Runtime getRuntime(){
       return Runtime.getRuntime();
    }

    /**
     * 获取当前空闲内存（字节）
     */
    public static long freeMemory(){
       return getRuntime().freeMemory();
    }

    /**
     * 已分配给 JVM 的内存（字节）
     * @return
     */
    public static long totalMemory(){
        return getRuntime().totalMemory();
    }

    /**
     * JVM 可用的最大内存（字节）
     * @return
     */
    public static long maxMemory(){
        return getRuntime().maxMemory();
    }

}
