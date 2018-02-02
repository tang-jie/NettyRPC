/**
 * Copyright (C) 2018 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newlandframework.rpc.spring;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors;
import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:BeanFactoryUtils.java
 * @description:BeanFactoryUtils功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/1
 */
public class BeanFactoryUtils implements BeanFactoryAware {
    private static BeanFactory beanFactory;

    private static boolean isContains(String[] values, String value) {
        if (value != null && value.length() > 0 && values != null && values.length > 0) {
            for (String v : values) {
                if (value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T getBean(String name) {
        if (beanFactory == null) {
            return null;
        }
        try {
            return (T) beanFactory.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    public static <T> T getOptionalBean(ListableBeanFactory beanFactory, String beanName, Class<T> beanType) {
        String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, beanType);
        if (!isContains(allBeanNames, beanName)) {
            return null;
        }
        Map<String, T> beansOfType = beansOfTypeIncludingAncestors(beanFactory, beanType);
        return beansOfType.get(beanName);
    }

    public static <T> List<T> getBeans(ListableBeanFactory beanFactory, String[] beanNames, Class<T> beanType) {
        String[] allBeanNames = beanNamesForTypeIncludingAncestors(beanFactory, beanType);
        List<T> beans = new ArrayList<T>(beanNames.length);
        for (String beanName : beanNames) {
            if (isContains(allBeanNames, beanName)) {
                beans.add(beanFactory.getBean(beanName, beanType));
            }
        }
        return Collections.unmodifiableList(beans);
    }

    @Override
    public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.beanFactory = factory;
    }
}

