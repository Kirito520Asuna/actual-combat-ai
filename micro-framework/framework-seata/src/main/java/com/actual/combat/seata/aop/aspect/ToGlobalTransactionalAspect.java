package com.actual.combat.seata.aop.aspect;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.seata.aop.annotation.ToGlobal;
import com.actual.combat.seata.properties.GlobalSeataProperties;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * @Author yan
 * @Date 2025/4/25 18:53:19
 * @Description
 */
@Aspect
@Slf4j
@Component
public class ToGlobalTransactionalAspect {
/*
    @Around("@annotation(transactional)")
    public Object aroundTransactional(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        // 检查是否已经存在全局事务
        String xid = RootContext.getXID();
        if (xid != null) {
            log.debug("Global transaction already exists. xid: {}", xid);
            // 如果已有全局事务，直接执行目标方法
            return joinPoint.proceed();
        }
        // 获取方法签名和目标方法
        Method method = getTargetMethod(joinPoint);
        Transactional txAnnotation = method.getAnnotation(Transactional.class);
        Class<? extends Throwable>[] rollbackFor = txAnnotation.rollbackFor();
        Class<? extends Throwable>[] noRollbackFor = txAnnotation.noRollbackFor();
        // 如果没有全局事务，创建新的全局事务
        GlobalTransaction globalTx = GlobalTransactionContext.getCurrentOrCreate();
        try {
            // 配置全局事务参数
            int resolveTimeout = resolveTimeout(txAnnotation);
            Propagation propagation = Propagation.valueOf(txAnnotation.propagation().name());

            // 开始全局事务
            globalTx.begin(resolveTimeout,method.getName());
            String globalTxXid = globalTx.getXid();
            log.debug("Begin global transaction. xid: {}", globalTxXid);
            // 执行目标方法
            Object result = joinPoint.proceed();
            // 如果执行成功，提交全局事务
            globalTx.commit();
            return result;
        } catch (Throwable e) {
            boolean shouldRollback = shouldRollback(e, Arrays.asList(rollbackFor), Arrays.asList(noRollbackFor));
            if (shouldRollback) {
                // 如果执行失败，回滚全局事务
                globalTx.rollback();
                log.error("Global transaction rollback. xid: {},e: {}", globalTx.getXid(), e.getMessage());
            }
            throw e;
        }
    }*/

    @Around("@annotation(io.seata.spring.annotation.GlobalTransactional) || @annotation(org.springframework.transaction.annotation.Transactional)")
    public Object aroundTransactional(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名和目标方法
        Method method = getTargetMethod(joinPoint);
        ToGlobal toGlobal = method.getAnnotation(ToGlobal.class);
        GlobalTransactional globalTxAnnotation = method.getAnnotation(GlobalTransactional.class);
        Transactional txAnnotation = method.getAnnotation(Transactional.class);
        boolean openToGlobal = null != toGlobal && toGlobal.open();

        if (null == toGlobal) {
            //全局配置
            try {
                GlobalSeataProperties properties = SpringUtil.getBean(GlobalSeataProperties.class);
                openToGlobal = properties.getToGlobal();
            } catch (Exception e) {
                log.error("获取全局配置失败", e);
            }
        }

        if (null != globalTxAnnotation) {
            if (openToGlobal && null != txAnnotation) {
                //  如果有 @ToGlobal 注解，并且 open = true，并且有 @Transactional 注解，执行全局事务逻辑 优先执行原生 @GlobalTransactional 注解逻辑
                log.debug("GlobalTransactional");
                return handleGlobalTransactional(joinPoint, globalTxAnnotation, method.getName());
            } else {
                // 如果有 @GlobalTransactional 注解 并有 @Transactional 注解，按spring的默认执行，执行本地事务逻辑
                log.debug("初始事务支持 {}", (txAnnotation != null ? txAnnotation.getClass() : globalTxAnnotation.getClass()));
                return joinPoint.proceed();
            }
        }

        Propagation propagation = txAnnotation.propagation();
        log.debug("传播行为 {} ", propagation);
        if (null != txAnnotation && !openToGlobal) {
            // 如果未开启本地事务转全局事务，保留原有事务执行
            // 如果没有 ToGlobal 注解或 open = false，保留 Spring 原生事务
            log.debug("未开启本地事务转全局事务，保留原有事务执行");
            return joinPoint.proceed();
        }
        // 检查是否为 Seata 支持的传播行为
        boolean isSeataSupported = isSeataSupportedPropagation(propagation);
        if (isSeataSupported) {
            log.debug("传播行为 {} Seata 支持", propagation);
            // Seata 支持的传播行为，执行全局事务逻辑
            Propagation seataPropagation = toSeataPropagation(propagation);
            return handleGlobalTransactional(joinPoint, txAnnotation, method.getName(), seataPropagation);
        } else {
            log.debug("传播行为 {} Seata 不支持", propagation);
            // Seata 不支持的传播行为，回退到本地事务
            //return handleLocalTransactional(joinPoint, txAnnotation);
            log.debug("初始事务支持 {}", txAnnotation.getClass());
            return joinPoint.proceed();
        }
    }

    /*================================================================<start>事务<start>=================================================================*/

    /**
     * 处理全局事务（Seata 支持的传播行为）
     */
    private Object handleGlobalTransactional(ProceedingJoinPoint joinPoint, Transactional txAnnotation,
                                             String name, Propagation seataPropagation) throws Throwable {
        String xid = RootContext.getXID();

        switch (seataPropagation) {
            case REQUIRED:
                return handleRequired(joinPoint, txAnnotation, name);
            case REQUIRES_NEW:
                return handleRequiresNew(joinPoint, txAnnotation, name, xid);
            case SUPPORTS:
                return handleSupports(joinPoint, xid);
            case NOT_SUPPORTED:
                return handleNotSupported(joinPoint, xid);
            case NEVER:
                return handleNever(joinPoint, xid);
            case MANDATORY:
                return handleMandatory(joinPoint, xid);
            default:
                throw new IllegalStateException("未知的 Seata 传播行为: " + seataPropagation);
        }
    }

    /**
     * 处理 @GlobalTransactional 注解逻辑
     */
    private Object handleGlobalTransactional(ProceedingJoinPoint joinPoint, GlobalTransactional globalTxAnnotation, String name) throws Throwable {
        GlobalTransaction globalTx = GlobalTransactionContext.getCurrentOrCreate();
        Class<? extends Throwable>[] rollbackFor = globalTxAnnotation.rollbackFor();
        Class<? extends Throwable>[] noRollbackFor = globalTxAnnotation.noRollbackFor();
        try {
            // 配置全局事务参数
            int timeoutMills = globalTxAnnotation.timeoutMills();
            // 开始全局事务
            globalTx.begin(timeoutMills, name);
            String globalTxXid = globalTx.getXid();
            log.debug("Begin global transaction. xid: {}", globalTxXid);
            // 执行目标方法
            Object result = joinPoint.proceed();
            // 如果执行成功，提交全局事务
            globalTx.commit();
            return result;
        } catch (Throwable e) {
            boolean shouldRollback = shouldRollback(e, Arrays.asList(rollbackFor), Arrays.asList(noRollbackFor));
            if (shouldRollback) {
                // 如果执行失败，回滚全局事务
                globalTx.rollback();
                log.error("Global transaction rollback. xid: {},e: {}", globalTx.getXid(), e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 处理本地事务（Seata 不支持的传播行为）
     */
    private Object handleLocalTransactional(ProceedingJoinPoint joinPoint, Transactional txAnnotation) throws Throwable {
        // 创建事务定义
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(txAnnotation.propagation().ordinal());
        def.setIsolationLevel(resolveIsolation(txAnnotation.isolation()));
        def.setTimeout(resolveLocalTimeout(txAnnotation));
        def.setReadOnly(txAnnotation.readOnly());
        PlatformTransactionManager transactionManager = SpringUtil.getBean(PlatformTransactionManager.class);
        // 开始本地事务
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            Object result = joinPoint.proceed();
            transactionManager.commit(status);
            return result;
        } catch (Throwable e) {
            boolean shouldRollback = shouldRollback(e, Arrays.asList(txAnnotation.rollbackFor()), Arrays.asList(txAnnotation.noRollbackFor()));
            if (shouldRollback) {
                transactionManager.rollback(status);
            }
            throw e;
        }
    }
    /*================================================================<-end->事务<-end->=================================================================*/
    /*================================================================<start>传播行为<start>=================================================================*/

    /**
     * 处理 REQUIRED 传播行为
     */
    private Object handleRequired(ProceedingJoinPoint joinPoint, Transactional txAnnotation, String methodName) throws Throwable {
        String xid = RootContext.getXID();
        if (xid != null) {
            // 如果已有全局事务，加入现有事务
            return joinPoint.proceed();
        }

        // 创建新事务
        return executeInNewTransaction(joinPoint, txAnnotation, methodName);
    }

    /**
     * 处理 REQUIRES_NEW 传播行为
     */
    private Object handleRequiresNew(ProceedingJoinPoint joinPoint, Transactional txAnnotation, String methodName, String xid) throws Throwable {
        if (xid != null) {
            // 暂停当前事务
            RootContext.unbind();
        }

        try {
            // 创建新事务
            return executeInNewTransaction(joinPoint, txAnnotation, methodName);
        } finally {
            // 恢复原事务（如果存在）
            if (xid != null) {
                RootContext.bind(xid);
            }
        }
    }

    /**
     * 处理 SUPPORTS 传播行为
     */
    private Object handleSupports(ProceedingJoinPoint joinPoint, String xid) throws Throwable {
        // 如果存在事务，加入；否则以非事务方式执行
        return joinPoint.proceed();
    }

    /**
     * 处理 NOT_SUPPORTED 传播行为
     */
    private Object handleNotSupported(ProceedingJoinPoint joinPoint, String xid) throws Throwable {
        if (xid != null) {
            // 暂停当前事务
            RootContext.unbind();
            try {
                return joinPoint.proceed();
            } finally {
                // 恢复原事务
                RootContext.bind(xid);
            }
        }
        // 无事务时直接执行
        return joinPoint.proceed();
    }

    /**
     * 处理 NEVER 传播行为
     */
    private Object handleNever(ProceedingJoinPoint joinPoint, String xid) throws Throwable {
        if (xid != null) {
            throw new IllegalStateException("NEVER 传播行为要求无事务，但当前存在全局事务: " + xid);
        }
        return joinPoint.proceed();
    }

    /**
     * 处理 MANDATORY 传播行为
     */
    private Object handleMandatory(ProceedingJoinPoint joinPoint, String xid) throws Throwable {
        if (xid == null) {
            throw new IllegalStateException("MANDATORY 传播行为要求存在事务，但当前无全局事务");
        }
        return joinPoint.proceed();
    }

    /**
     * 执行新全局事务
     */
    private Object executeInNewTransaction(ProceedingJoinPoint joinPoint, Transactional txAnnotation, String name) throws Throwable {
        GlobalTransaction globalTx = GlobalTransactionContext.getCurrentOrCreate();
        Class<? extends Throwable>[] rollbackFor = txAnnotation.rollbackFor();
        Class<? extends Throwable>[] noRollbackFor = txAnnotation.noRollbackFor();
        try {
            // 配置事务参数
            int timeoutMills = resolveTimeout(txAnnotation);
            // 开始全局事务
            globalTx.begin(timeoutMills, name);
            String globalTxXid = globalTx.getXid();
            log.debug("Begin global transaction. xid: {}", globalTxXid);
            // 执行目标方法
            Object result = joinPoint.proceed();
            // 如果执行成功，提交全局事务
            globalTx.commit();
            return result;
        } catch (Throwable e) {
            boolean shouldRollback = shouldRollback(e, Arrays.asList(rollbackFor), Arrays.asList(noRollbackFor));
            if (shouldRollback) {
                // 如果执行失败，回滚全局事务
                globalTx.rollback();
                log.error("Global transaction rollback. xid: {},e: {}", globalTx.getXid(), e.getMessage());
            }
            throw e;
        }
    }

    /*================================================================<-end->传播行为<-end->=================================================================*/

    /**
     * 检查是否为 Seata 支持的传播行为
     */
    private boolean isSeataSupportedPropagation(org.springframework.transaction.annotation.Propagation springPropagation) {
        return springPropagation != org.springframework.transaction.annotation.Propagation.NESTED;
    }

    /**
     * 映射 Spring 的 Propagation 到 Seata 的 Propagation
     */
    private Propagation toSeataPropagation(org.springframework.transaction.annotation.Propagation springPropagation) {
        switch (springPropagation) {
            case REQUIRED:
                return Propagation.REQUIRED;
            case REQUIRES_NEW:
                return Propagation.REQUIRES_NEW;
            case SUPPORTS:
                return Propagation.SUPPORTS;
            case NOT_SUPPORTED:
                return Propagation.NOT_SUPPORTED;
            case NEVER:
                return Propagation.NEVER;
            case MANDATORY:
                return Propagation.MANDATORY;
            case NESTED:
                // Seata 不支持 NESTED，返回默认值或抛出异常
                throw new UnsupportedOperationException(String.format("%s 不支持 %s 传播行为", "Seata", springPropagation));
            default:
                return Propagation.REQUIRED;
        }
    }


    /**
     * 获取目标方法
     */
    private Method getTargetMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        return targetClass.getMethod(methodName, parameterTypes);
    }

    /**
     * 解析超时时间
     */
    private int resolveLocalTimeout(Transactional transactional) {
        // 优先使用 timeoutString（如果非空）
        if (!transactional.timeoutString().isEmpty()) {
            try {
                return Integer.parseInt(transactional.timeoutString()); // 秒转毫秒
            } catch (NumberFormatException ignored) {
            }
        }
        // 其次使用 timeout（如果不是默认值）
        if (transactional.timeout() != -1) {
            return transactional.timeout(); // 秒转毫秒
        }
        // 最后使用默认值
        return -1;
    }

    /**
     * 解析超时时间
     */
    private int resolveTimeout(Transactional transactional) {
        // 优先使用 timeoutString（如果非空）
        if (!transactional.timeoutString().isEmpty()) {
            try {
                return Integer.parseInt(transactional.timeoutString()) * 1000; // 秒转毫秒
            } catch (NumberFormatException ignored) {
            }
        }
        // 其次使用 timeout（如果不是默认值）
        if (transactional.timeout() != -1) {
            return transactional.timeout() * 1000; // 秒转毫秒
        }
        Environment environment = SpringUtil.getBean(Environment.class);
        // 从配置文件读取默认超时时间（毫秒）
        int defaultTimeoutMills = environment.getProperty("seata.global-transaction.timeout-mills", Integer.class, 60000).intValue();
        // 最后使用默认值
        return defaultTimeoutMills;
    }

    /**
     * 解析隔离级别
     */
    private int resolveIsolation(Isolation isolation) {
        switch (isolation) {
            case READ_UNCOMMITTED:
                return TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
            case READ_COMMITTED:
                return TransactionDefinition.ISOLATION_READ_COMMITTED;
            case REPEATABLE_READ:
                return TransactionDefinition.ISOLATION_REPEATABLE_READ;
            case SERIALIZABLE:
                return TransactionDefinition.ISOLATION_SERIALIZABLE;
            default:
                return TransactionDefinition.ISOLATION_DEFAULT;
        }
    }

    /**
     * 判断是否需要回滚
     */
    private boolean shouldRollback(Throwable e, Collection<Class<? extends Throwable>> rollbackFor,
                                   Collection<Class<? extends Throwable>> noRollbackFor) {
        // 如果异常在 noRollbackFor 中，则不回滚
        for (Class<? extends Throwable> noRollback : noRollbackFor) {
            if (noRollback.isInstance(e)) {
                return false;
            }
        }

        // 如果 rollbackFor 不为空，检查是否匹配
        if (!rollbackFor.isEmpty()) {
            for (Class<? extends Throwable> rollback : rollbackFor) {
                if (rollback.isInstance(e)) {
                    return true;
                }
            }
            return false;
        }

        // 默认行为：对 RuntimeException 和 Error 回滚
        return e instanceof RuntimeException || e instanceof Error;
    }
}