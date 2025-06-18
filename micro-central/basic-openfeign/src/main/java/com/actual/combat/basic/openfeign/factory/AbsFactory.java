package com.actual.combat.basic.openfeign.factory;

import cn.hutool.core.collection.CollUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.openfeign.factory.interfaces.AbsClient;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @author yan
 */
public class AbsFactory implements BeanFactoryAware, InitializingBean, AbsBean {
    private ConfigurableListableBeanFactory beanFactory;
    private static List<AbsClient> absClients = CollUtil.newArrayList();

    public static AbsClient getClient(AbsEnum absEnum) {
        for (AbsClient absClient : absClients) {
            if (absClient.support(absEnum)) {
                return absClient;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log().debug("abstractClients init ...");
        Map<String, AbsClient> beansOfType = beanFactory.getBeansOfType(AbsClient.class);
        beansOfType.keySet().forEach(key -> {
            if (beanFactory.containsBeanDefinition(key)) {
                if (beanFactory.getBeanDefinition(key).isPrimary()) {
                    absClients.add(beansOfType.get(key));
                }
            } else if (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
                if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).containsBeanDefinition(key)) {
                    if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).getBeanDefinition(key).isPrimary()) {
                        absClients.add(beansOfType.get(key));
                    }
                }
            }
        });
    }
}
