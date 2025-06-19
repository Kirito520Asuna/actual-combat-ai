package com.actual.combat.basic.file.core.storage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/3/7 20:37:33
 * @Description
 */
public class FileFactory implements BeanFactoryAware, InitializingBean, AbsBean {
    private ConfigurableListableBeanFactory beanFactory;
    private static List<IFileStorageClient> storages = CollUtil.newArrayList();
    private static int index = 0;

    private static void initStorages() {
        if (index == 0) {
            Map<String, IFileStorageClient> beansOfType = SpringUtil.getBeansOfType(IFileStorageClient.class);
            beansOfType.forEach((key, value) -> {
                storages.add(value);
            });
            index++;
        }
    }

    public static IFileStorageClient getClient(StorageType storageType) {
        initStorages();
        for (IFileStorageClient storage : storages) {
            if (storage.support(storageType)) {
                return storage;
            }
        }
        throw new IllegalArgumentException();
    }


    @Override
    @PostConstruct
    public void init() {
        AbsBean.super.init();
        initStorages();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, IFileStorageClient> beansOfType = beanFactory.getBeansOfType(IFileStorageClient.class);
        beansOfType.keySet().forEach(key -> {
            if (beanFactory.containsBeanDefinition(key)) {
                if (beanFactory.getBeanDefinition(key).isPrimary()) {
                    storages.add(beansOfType.get(key));
                }
            } else if (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
                if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).containsBeanDefinition(key)) {
                    if (((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory()).getBeanDefinition(key).isPrimary()) {
                        storages.add(beansOfType.get(key));
                    }
                }
            }
        });
    }
}
