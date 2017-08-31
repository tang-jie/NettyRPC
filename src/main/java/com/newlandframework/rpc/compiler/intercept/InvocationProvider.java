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
package com.newlandframework.rpc.compiler.intercept;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:InvocationProvider.java
 * @description:InvocationProvider功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/8/30
 */
public class InvocationProvider implements Invocation {
    private final Method method;
    private final Object[] arguments;
    private final Object proxy;
    private final Object target;

    public InvocationProvider(final Object target, final Object proxy, final Method method, final Object[] arguments) {
        Object[] objects = ArrayUtils.clone(arguments);
        this.method = method;
        this.arguments = objects == null ? new Object[0] : objects;
        this.proxy = proxy;
        this.target = target;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object proceed() throws Throwable {
        try {
            return method.invoke(target, arguments);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}

