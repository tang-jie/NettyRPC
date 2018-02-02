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
package com.newlandframework.rpc.filter;

import com.newlandframework.rpc.core.Modular;
import com.newlandframework.rpc.core.ModuleInvoker;
import com.newlandframework.rpc.core.ModuleProvider;
import com.newlandframework.rpc.model.MessageRequest;

import java.util.List;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleFilterChainWrapper.java
 * @description:ModuleFilterChainWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/2
 */
public class ModuleFilterChainWrapper implements Modular {
    private Modular modular;
    private List<ChainFilter> filters;

    public ModuleFilterChainWrapper(Modular modular) {
        if (modular == null) {
            throw new IllegalArgumentException("module is null");
        }
        this.modular = modular;
    }

    @Override
    public <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker, MessageRequest request) {
        return modular.invoke(buildChain(invoker), request);
    }

    private <T> ModuleInvoker<T> buildChain(ModuleInvoker<T> invoker) {
        ModuleInvoker last = invoker;

        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                ChainFilter filter = filters.get(i);
                ModuleInvoker<T> next = last;
                last = new ModuleInvoker<T>() {
                    @Override
                    public Object invoke(MessageRequest request) throws Throwable {
                        return filter.invoke(next, request);
                    }

                    @Override
                    public Class<T> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public String toString() {
                        return invoker.toString();
                    }

                    @Override
                    public void destroy() {
                        invoker.destroy();
                    }
                };
            }
        }
        return last;
    }

    public List<ChainFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ChainFilter> filters) {
        this.filters = filters;
    }
}

