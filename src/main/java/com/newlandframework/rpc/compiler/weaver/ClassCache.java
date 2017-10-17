/**
 * Copyright (C) 2017 Newland Group Holding Limited
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
package com.newlandframework.rpc.compiler.weaver;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ClassCache.java
 * @description:ClassCache功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/8/30
 */
public class ClassCache {
    // FIXME: 2017/8/30 by tangjie
    // 引入弱引用节省内存消耗，JVM每次gc的时候，自动释放内存
    private final Map<ClassLoader, Map<Set<Class<?>>, WeakReference<Class<?>>>> loader = new HashMap<ClassLoader, Map<Set<Class<?>>, WeakReference<Class<?>>>>();

    private final Transformer transformer;

    public ClassCache(Transformer transformer) {
        this.transformer = transformer;
    }

    private Map<Set<Class<?>>, WeakReference<Class<?>>> getClassCache(ClassLoader classLoader) {
        Map<Set<Class<?>>, WeakReference<Class<?>>> cache = loader.get(classLoader);
        if (cache == null) {
            cache = new HashMap<Set<Class<?>>, WeakReference<Class<?>>>(512);
            loader.put(classLoader, cache);
        }

        return cache;
    }

    private Set<Class<?>> toClassCacheKey(Class<?>[] proxyClasses) {
        return new HashSet<Class<?>>(Arrays.asList(proxyClasses));
    }

    public synchronized Class<?> getProxyClass(ClassLoader classLoader, Class<?>[] proxyClasses) {
        Map<Set<Class<?>>, WeakReference<Class<?>>> classCache = getClassCache(classLoader);
        Set<Class<?>> key = toClassCacheKey(proxyClasses);
        Class<?> proxyClass;
        Reference<Class<?>> proxyClassReference = classCache.get(key);

        if (proxyClassReference == null) {
            proxyClass = transformer.transform(classLoader, proxyClasses);
            classCache.put(key, new WeakReference<Class<?>>(proxyClass));
        } else {
            proxyClass = proxyClassReference.get();
            if (proxyClass == null) {
                proxyClass = transformer.transform(classLoader, proxyClasses);
                classCache.put(key, new WeakReference<Class<?>>(proxyClass));
            }
        }

        return proxyClass;
    }
}

