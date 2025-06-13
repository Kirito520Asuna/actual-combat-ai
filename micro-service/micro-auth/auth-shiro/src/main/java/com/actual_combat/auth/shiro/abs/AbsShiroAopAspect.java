package com.actual_combat.auth.shiro.abs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.aspect.AbsSysLog;
import com.actual_combat.auth.shiro.aop.ShiroLogical;
import com.actual_combat.auth.shiro.aop.ShiroPermissions;
import com.actual_combat.auth.shiro.aop.ShiroRoles;
import com.actual_combat.auth.shiro.config.ShiroAnnotationConfig;
import com.actual_combat.base.core.config.auth.AuthorizationConfig;
import com.actual_combat.base.core.constant.Roles;
import com.actual_combat.base.enums.ApiCode;
import com.actual_combat.base.exceptions.GlobalCustomException;
import com.actual_combat.base.utils.object.ObjectUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.aspectj.lang.JoinPoint;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/10/21 下午2:10:02
 * @Description
 */
public interface AbsShiroAopAspect extends AbsSysLog {
    default void hasRolesPermissions(JoinPoint joinPoint) throws Exception {
        ShiroAnnotationConfig shiroAnnotationConfig = SpringUtil.getBean(ShiroAnnotationConfig.class);
        //全局控制
        Logical logical = shiroAnnotationConfig.getLogical();
        if (shiroAnnotationConfig.isEnable()) {
            //灵活控制注解 逻辑
            ShiroLogical shiroLogical = getAnnotation(joinPoint, ShiroLogical.class);
            if (ObjectUtil.isNotEmpty(shiroLogical)) {
                logical = shiroLogical.logical();
                log().debug("启用灵活逻辑控制：{}", logical);
            }
            switch (logical) {
                case OR:
                    hasRolesOrPermissions(joinPoint);
                    break;
                case AND:
                default:
                    hasRolesAndPermissions(joinPoint);
                    break;
            }
        } else {
            log().warn("未开启权限认证");
        }
    }

    /**
     * 俩者需要同时满足才通过
     *
     * @param joinPoint
     * @throws Exception
     */
    default void hasRolesAndPermissions(JoinPoint joinPoint) throws Exception {
        hasRoles(joinPoint);
        hasPermissions(joinPoint);
    }

    /**
     * 俩者只要有一个满足就通过
     *
     * @param joinPoint
     * @throws Exception
     */
    default void hasRolesOrPermissions(JoinPoint joinPoint) throws Exception {
        Exception hasRolesException = null;
        //List<Boolean> booleans = CollUtil.newArrayList();
        try {
            hasRoles(joinPoint);
        } catch (Exception e) {
            hasRolesException = e;
            //booleans.add(Boolean.FALSE);
        }

        if (ObjectUtil.isNotEmpty(hasRolesException)) {
            //存在说明上述校验未通过 继续验证权限
            try {
                hasPermissions(joinPoint);
            } catch (Exception e) {
                // 抛出上个异常信息
                throw hasRolesException;
            }
        }
    }

    /**
     * 验证角色权限
     *
     * @param joinPoint
     * @throws Exception
     */
    default boolean hasRoles(JoinPoint joinPoint) throws Exception {
        ShiroRoles shiroRoles = getAnnotation(joinPoint, ShiroRoles.class);
        String name = null;
        String simpleName = null;
        String[] value = {};
        Logical logical = Logical.AND;

        if (ObjectUtil.isNotEmpty(shiroRoles)) {
            log().debug("角色权限校验");
            name = shiroRoles.getClass().getName();
            simpleName = shiroRoles.getClass().getSimpleName();
            value = shiroRoles.value();
            logical = shiroRoles.logical();
        }
        return hasAnnotation(shiroRoles, name, simpleName, value, logical);
    }

    /**
     * 验证权限
     *
     * @param joinPoint
     * @throws Exception
     */
    default boolean hasPermissions(JoinPoint joinPoint) throws Exception {
        ShiroPermissions shiroPermissions = getAnnotation(joinPoint, ShiroPermissions.class);
        String name = null;
        String simpleName = null;
        String[] value = {};
        Logical logical = Logical.AND;

        if (ObjectUtil.isNotEmpty(shiroPermissions)) {
            log().debug("权限校验");
            name = shiroPermissions.getClass().getName();
            simpleName = shiroPermissions.getClass().getSimpleName();
            value = shiroPermissions.value();
            logical = shiroPermissions.logical();
        }
        return hasAnnotation(shiroPermissions, name, simpleName, value, logical);
    }

    /**
     * 存在注解--验证权限
     *
     * @param shiroRolesOrPermissions
     * @param name
     * @param simpleName
     * @param value
     * @param logical
     * @param <T>
     * @throws Exception
     */
    default <T extends Annotation> boolean hasAnnotation(T shiroRolesOrPermissions, String name, String simpleName, String[] value, Logical logical) throws Exception {

        boolean hasAnnotation = ObjectUtil.isNotEmpty(shiroRolesOrPermissions);
        if (!hasAnnotation) {
            return hasAnnotation;
        }

        Class<T> aclass = (Class<T>) shiroRolesOrPermissions.getClass();
        //aclass.reflectionData.get().interfaces[0]
        List<Class<?>> classInterfacesList = CollUtil.newArrayList();
        try {
            classInterfacesList.addAll(Arrays.stream(aclass.getInterfaces()).collect(Collectors.toList()));
        } catch (Exception e) {
            log().warn("warn : {}", e.getMessage());
        }

        List<String> keys = CollUtil.newArrayList();
        try {
            keys.addAll(Arrays.stream(value).filter(ObjectUtil::isNotEmpty).map(o -> {
                        String re = o;
                        if (classInterfacesList.contains(ShiroRoles.class)) {
                            re = re.startsWith(Roles.roles) ? re :
                                    new StringBuffer(Roles.roles).append(re).toString();
                        } else if (classInterfacesList.contains(ShiroPermissions.class)) {
                            re = re.startsWith(Roles.perms) ? re :
                                    new StringBuffer(Roles.perms).append(re).toString();
                        }
                        return re;
                    }
            ).collect(Collectors.toList()));
        } catch (Exception e) {
            log().error("class:{},error:{}", simpleName, e.getMessage());
            throw new Exception(name + " value must have value and not null");
        }
        boolean pass = checkPass(logical, keys, aclass);

        String msg = "";
        if (ObjectUtil.isEmpty(aclass)) {
        } else if (classInterfacesList.contains(ShiroRoles.class)) {
            msg = "角色";
        } else if (classInterfacesList.contains(ShiroPermissions.class)) {
            msg = "权限";
        }
        if (!pass) {
            log().debug("{}校验失败", msg);
            throw new GlobalCustomException(ApiCode.FORBIDDEN);
        }
        log().debug("{}校验通过", msg);
        return hasAnnotation;

    }

    /**
     * 获取权限列表
     *
     * @return
     */
    default <T extends Annotation> List<String> getKeyList(Class<T> shiroRolesOrPermissionClass) {
        List<String> keys = CollUtil.newArrayList();
        List<Class<?>> classInterfacesList = CollUtil.newArrayList();
        try {
            classInterfacesList.addAll(Arrays.stream(shiroRolesOrPermissionClass.getInterfaces()).collect(Collectors.toList()));
        } catch (Exception e) {
            log().warn("warn : {}", e.getMessage());
        }
        if (classInterfacesList.contains(ShiroRoles.class)) {
            keys.addAll(getRoles());
            keys = keys.stream().map(s -> s.startsWith(Roles.roles) ? s : new StringBuffer(Roles.roles).append(s).toString()).collect(Collectors.toList());
        } else if (classInterfacesList.contains(ShiroPermissions.class)) {
            keys.addAll(getPermissions());
            keys = keys.stream().map(s -> s.startsWith(Roles.perms) ? s : new StringBuffer(Roles.perms).append(s).toString()).collect(Collectors.toList());
        }
        return keys;
    }

    default List<String> getRoles() {
        return CollUtil.newArrayList();
    }

    default List<String> getPermissions() {
        return CollUtil.newArrayList();
    }

    /**
     * 验证通过逻辑
     *
     * @param logical
     * @param rolesOrPermissionsKeys
     * @return
     */
    default <T extends Annotation> boolean checkPass(Logical logical, List<String> rolesOrPermissionsKeys, Class<T> shiroRolesOrPermissionsClass) {
        boolean pass;
        List<String> keyList = getKeyList(shiroRolesOrPermissionsClass);
        switch (logical) {
            case OR:
                pass = false;
                break;
            case AND:
            default:
                pass = true;
                break;
        }

        if (isAdmin(getRoles())) {
            pass = true;
        } else {
            for (String key : rolesOrPermissionsKeys) {
                if (ObjectUtil.equal(Logical.OR, logical)) {
                    if (keyList.contains(key)) {
                        pass = true;
                        break;
                    }
                } else {
                    if (!pass) {
                        break;
                    } else {
                        pass = keyList.contains(key) && pass;
                    }
                }
            }
        }
        return pass;
    }

    default boolean isAdmin(List<String> keys) {
        keys = ObjectUtils.defaultIfEmpty(keys, new ArrayList<>());
        boolean isAdmin = false;

        for (String key : keys) {
            isAdmin = isAdmin || isAdmin(key);
            if (isAdmin) {
                break;
            }
        }
        return isAdmin;
    }


    /**
     * @param key
     * @return
     */
    default boolean isAdmin(String key) {
        AuthorizationConfig config = SpringUtil.getBean(AuthorizationConfig.class);
        return config.isAdmin(key);
    }

}
