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

import com.newlandframework.rpc.core.ModuleInvoker;
import com.newlandframework.rpc.core.ModuleProvider;
import com.newlandframework.rpc.model.MessageRequest;

import java.util.List;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleProviderWrapper.java
 * @description:ModuleProviderWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/1/31
 */
public class ModuleProviderWrapper<T> implements ModuleProvider<T> {
    private ModuleProvider<T> provider;
    private MessageRequest request;
    private List<ModuleListener> listeners;

    public ModuleProviderWrapper(ModuleProvider<T> provider, List<ModuleListener> listeners, MessageRequest request) {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null");
        }
        this.provider = provider;
        this.listeners = listeners;
        this.request = request;
        if (listeners != null && listeners.size() > 0) {
            RuntimeException exception = null;
            for (ModuleListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.exported(this, request);
                    } catch (RuntimeException t) {
                        exception = t;
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    @Override
    public ModuleInvoker<T> getInvoker() {
        return provider.getInvoker();
    }

    @Override
    public void destoryInvoker() {
        try {
            provider.destoryInvoker();
        } finally {
            if (listeners != null && listeners.size() > 0) {
                RuntimeException exception = null;
                for (ModuleListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.unExported(this, request);
                        } catch (RuntimeException t) {
                            exception = t;
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
            }
        }
    }
}

