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

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MethodDescriptor.java
 * @description:MethodDescriptor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/8/30
 */
public class MethodDescriptor {
    private static final Map<Class<?>, Character> BUILDER = new ImmutableMap.Builder<Class<?>, Character>()
            .put(Boolean.TYPE, Character.valueOf('Z'))
            .put(Byte.TYPE, Character.valueOf('B'))
            .put(Short.TYPE, Character.valueOf('S'))
            .put(Integer.TYPE, Character.valueOf('I'))
            .put(Character.TYPE, Character.valueOf('C'))
            .put(Long.TYPE, Character.valueOf('J'))
            .put(Float.TYPE, Character.valueOf('F'))
            .put(Double.TYPE, Character.valueOf('D'))
            .put(Void.TYPE, Character.valueOf('V'))
            .build();

    private final String internal;

    public MethodDescriptor(Method method) {
        final StringBuilder buf = new StringBuilder(method.getName()).append('(');
        for (Class<?> p : method.getParameterTypes()) {
            appendTo(buf, p);
        }

        buf.append(')');
        this.internal = buf.toString();
    }

    private static void appendTo(StringBuilder buf, Class<?> type) {
        if (type.isPrimitive()) {
            buf.append(BUILDER.get(type));
        } else if (type.isArray()) {
            buf.append('[');
            appendTo(buf, type.getComponentType());
        } else {
            buf.append('L').append(type.getName().replace('.', '/')).append(';');
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (o.getClass() != getClass()) {
            return false;
        }

        MethodDescriptor other = (MethodDescriptor) o;
        return other.internal.equals(internal);
    }

    @Override
    public int hashCode() {
        return internal.hashCode();
    }

    @Override
    public String toString() {
        return internal;
    }
}

