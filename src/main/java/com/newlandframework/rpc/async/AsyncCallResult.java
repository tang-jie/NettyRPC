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
import com.newlandframework.rpc.exception.AsyncCallException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AsyncCallResult.java
 * @description:AsyncCallResult功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class AsyncCallResult {
    private Class returnClass;
    private Future future;
    private Long timeout;

    public AsyncCallResult(Class returnClass, Future future, Long timeout) {
        this.returnClass = returnClass;
        this.future = future;
        this.timeout = timeout;
    }

    public Object loadFuture() throws AsyncCallException {
        try {
            if (timeout <= 0L) {
                return future.get();
            } else {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new AsyncCallException(e);
        } catch (InterruptedException e) {
            throw new AsyncCallException(e);
        } catch (Exception e) {
            throw new AsyncCallException(e);
        }
    }

    public Object getResult() {
        Class proxyClass = AsyncProxyCache.get(returnClass.getName());
        if (proxyClass == null) {
            Enhancer enhancer = new Enhancer();
            if (returnClass.isInterface()) {
                enhancer.setInterfaces(new Class[]{AsyncCallObject.class, returnClass});
            } else {
                enhancer.setInterfaces(new Class[]{AsyncCallObject.class});
                enhancer.setSuperclass(returnClass);
            }
            enhancer.setCallbackFilter(new AsyncCallFilter());
            enhancer.setCallbackTypes(new Class[]{AsyncCallResultInterceptor.class, AsyncCallObjectInterceptor.class});
            proxyClass = enhancer.createClass();
            AsyncProxyCache.save(returnClass.getName(), proxyClass);
        }

        Enhancer.registerCallbacks(proxyClass, new Callback[]{new AsyncCallResultInterceptor(this),
                new AsyncCallObjectInterceptor(future)});

        try {
            return ReflectionUtils.newInstance(proxyClass);
        } finally {
            Enhancer.registerStaticCallbacks(proxyClass, null);
        }
    }
}

