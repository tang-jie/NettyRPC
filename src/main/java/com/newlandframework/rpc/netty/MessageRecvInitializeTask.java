/**
 * Copyright (C) 2016 Newland Group Holding Limited
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
package com.newlandframework.rpc.netty;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.event.*;
import com.newlandframework.rpc.event.AbstractInvokeEventBus.ModuleEvent;
import com.newlandframework.rpc.filter.ServiceFilterBinder;
import com.newlandframework.rpc.jmx.ModuleMetricsHandler;
import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.parallel.SemaphoreWrapperFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MessageRecvInitializeTask.java
 * @description:MessageRecvInitializeTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MessageRecvInitializeTask extends AbstractMessageRecvInitializeTask {
    private AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<ModuleMetricsVisitor>();
    private AtomicReference<InvokeEventBusFacade> facade = new AtomicReference<InvokeEventBusFacade>();
    private AtomicReference<InvokeEventWatcher> watcher = new AtomicReference<InvokeEventWatcher>(new InvokeEventWatcher());
    private SemaphoreWrapperFactory factory = SemaphoreWrapperFactory.getInstance();

    public MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        super(request, response, handlerMap);
    }

    @Override
    protected void injectInvoke() {
        Class cls = handlerMap.get(request.getClassName()).getClass();
        boolean binder = ServiceFilterBinder.class.isAssignableFrom(cls);
        if (binder) {
            cls = ((ServiceFilterBinder) handlerMap.get(request.getClassName())).getObject().getClass();
        }

        ReflectionUtils utils = new ReflectionUtils();

        try {
            Method method = ReflectionUtils.getDeclaredMethod(cls, request.getMethodName(), request.getTypeParameters());
            utils.listMethod(method, false);
            String signatureMethod = utils.getProvider().toString();
            visitor.set(ModuleMetricsHandler.getInstance().visit(request.getClassName(), signatureMethod));
            facade.set(new InvokeEventBusFacade(ModuleMetricsHandler.getInstance(), visitor.get().getModuleName(), visitor.get().getMethodName()));
            watcher.get().addObserver(new InvokeObserver(facade.get(), visitor.get()));
            watcher.get().watch(ModuleEvent.INVOKE_EVENT);
        } finally {
            utils.clearProvider();
        }
    }

    @Override
    protected void injectSuccInvoke(long invokeTimespan) {
        watcher.get().addObserver(new InvokeSuccObserver(facade.get(), visitor.get(), invokeTimespan));
        watcher.get().watch(ModuleEvent.INVOKE_SUCC_EVENT);
    }

    @Override
    protected void injectFailInvoke(Throwable error) {
        watcher.get().addObserver(new InvokeFailObserver(facade.get(), visitor.get(), error));
        watcher.get().watch(ModuleEvent.INVOKE_FAIL_EVENT);
    }

    @Override
    protected void injectFilterInvoke() {
        watcher.get().addObserver(new InvokeFilterObserver(facade.get(), visitor.get()));
        watcher.get().watch(ModuleEvent.INVOKE_FILTER_EVENT);
    }

    @Override
    protected void acquire() {
        factory.acquire();
    }

    @Override
    protected void release() {
        factory.release();
    }
}
