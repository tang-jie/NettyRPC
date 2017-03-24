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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AsyncCallObjectInterceptor.java
 * @description:AsyncCallObjectInterceptor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class AsyncCallObjectInterceptor implements MethodInterceptor {
    private static final String NETTYRPCSTATUS = "_getStatus";
    private Future future;

    public AsyncCallObjectInterceptor(Future future) {
        this.future = future;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
        if (NETTYRPCSTATUS.equals(method.getName())) {
            return getStatus();
        }
        return null;
    }

    private Object getStatus() {
        long startTime = 0L;
        long endTime = 0L;
        if (future instanceof AsyncFuture) {
            startTime = ((AsyncFuture) future).getStartTime();
            endTime = ((AsyncFuture) future).getEndTime();
        }

        CallStatus status = null;

        if (future.isCancelled()) {
            status = CallStatus.TIMEOUT;
        } else if (future.isDone()) {
            status = CallStatus.DONE;
        } else {
            status = CallStatus.RUN;
            if (endTime == 0) {
                endTime = System.currentTimeMillis();
            }
        }

        return new AsyncCallStatus(startTime, (endTime - startTime), status);
    }
}

