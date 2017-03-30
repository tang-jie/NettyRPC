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
package com.newlandframework.rpc.compiler;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.util.List;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ClassProxy.java
 * @description:ClassProxy功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/30
 */
public class ClassProxy {
    public <T> Class<T> createDynamicSubclass(Class<T> superclass) {
        Enhancer e = new Enhancer() {
            @Override
            protected void filterConstructors(Class sc, List constructors) {
                // FIXME: 2017/3/30 by tangjie
                // maybe change javassist support
            }
        };

        if (superclass.isInterface()) {
            e.setInterfaces(new Class[]{superclass});
        } else {
            e.setSuperclass(superclass);
        }

        e.setCallbackType(NoOp.class);
        Class<T> proxyClass = e.createClass();
        return proxyClass;
    }
}

