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
package com.newlandframework.rpc.async;

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.exception.AsyncCallException;
import com.newlandframework.rpc.parallel.RpcThreadPool;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AsyncInvoker.java
 * @description:AsyncInvoker功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class AsyncInvoker {
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS, RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS);

    public <R> R submit(final AsyncCallback<R> callback) {
        Type type = callback.getClass().getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            Class returnClass = (Class) ReflectionUtils.getGenericClass((ParameterizedType) type, 0);
            return intercept(callback, returnClass);
        } else {
            throw new AsyncCallException("NettyRPC AsyncCallback must be parameterized type!");
        }
    }


    private <T> AsyncFuture<T> submit(Callable<T> task) {
        AsyncFuture future = new AsyncFuture<T>(task);
        executor.submit(future);
        return future;
    }

    private <R> R intercept(final AsyncCallback<R> callback, Class<?> returnClass) {
        if (!Modifier.isPublic(returnClass.getModifiers())) {
            return callback.call();
        } else if (Modifier.isFinal(returnClass.getModifiers())) {
            return callback.call();
        } else if (Void.TYPE.isAssignableFrom(returnClass)) {
            return callback.call();
        } else if (returnClass.isPrimitive() || returnClass.isArray()) {
            return callback.call();
        } else if (returnClass == Object.class) {
            return callback.call();
        } else {
            return submit(callback, returnClass);
        }
    }

    private <R> R submit(final AsyncCallback<R> callback, Class<?> returnClass) {
        Future future = submit(new Callable() {
            public R call() throws Exception {
                return callback.call();
            }
        });

        AsyncCallResult result = new AsyncCallResult(returnClass, future, RpcSystemConfig.SYSTEM_PROPERTY_ASYNC_MESSAGE_CALLBACK_TIMEOUT);
        R asyncProxy = (R) result.getResult();

        return asyncProxy;
    }
}

