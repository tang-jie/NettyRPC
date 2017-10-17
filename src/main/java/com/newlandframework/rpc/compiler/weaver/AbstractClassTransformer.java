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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AbstractClassTransformer.java
 * @description:AbstractClassTransformer功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/16
 */
public abstract class AbstractClassTransformer implements Transformer {
    @Override
    public Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses) {
        return null;
    }

    protected Method[] findImplementationMethods(Class<?>[] proxyClasses) {
        Map<MethodDescriptor, Method> descriptorMap = new HashMap<MethodDescriptor, Method>(1024);
        Set<MethodDescriptor> finalSet = new HashSet<MethodDescriptor>();

        for (int i = 0; i < proxyClasses.length; i++) {
            Class<?> proxyInterface = proxyClasses[i];
            Method[] methods = proxyInterface.getMethods();
            for (int j = 0; j < methods.length; j++) {
                MethodDescriptor descriptor = new MethodDescriptor(methods[j]);
                if (Modifier.isFinal(methods[j].getModifiers())) {
                    finalSet.add(descriptor);
                } else if (!descriptorMap.containsKey(descriptor)) {
                    descriptorMap.put(descriptor, methods[j]);
                }
            }
        }

        Collection<Method> results = descriptorMap.values();
        for (MethodDescriptor signature : finalSet) {
            results.remove(descriptorMap.get(signature));
        }

        return results.toArray(new Method[results.size()]);
    }
}
