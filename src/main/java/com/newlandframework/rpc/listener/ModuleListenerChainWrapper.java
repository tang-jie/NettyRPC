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
package com.newlandframework.rpc.listener;

import com.newlandframework.rpc.core.Modular;
import com.newlandframework.rpc.core.ModuleInvoker;
import com.newlandframework.rpc.core.ModuleProvider;
import com.newlandframework.rpc.model.MessageRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleListenerChainWrapper.java
 * @description:ModuleListenerChainWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/2
 */
public class ModuleListenerChainWrapper implements Modular {
    private Modular modular;
    private List<ModuleListener> listeners;

    public ModuleListenerChainWrapper(Modular modular) {
        if (modular == null) {
            throw new IllegalArgumentException("module is null");
        }
        this.modular = modular;
    }

    @Override
    public <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker, MessageRequest request) {
        return new ModuleProviderWrapper(modular.invoke(invoker, request), Collections.unmodifiableList(listeners), request);
    }

    public List<ModuleListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ModuleListener> listeners) {
        this.listeners = listeners;
    }
}
