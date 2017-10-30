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
package com.newlandframework.rpc.netty;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RecvInitializeTaskFacade.java
 * @description:RecvInitializeTaskFacade功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/27
 */
public class RecvInitializeTaskFacade {
    private MessageRequest request;
    private MessageResponse response;
    private Map<String, Object> handlerMap;
    private boolean isMetrics = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT;
    private boolean jmxMetricsHash = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT;

    public RecvInitializeTaskFacade(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    public Callable<Boolean> getTask() {
        return isMetrics ? getMetricsTask() : new MessageRecvInitializeTaskAdapter(request, response, handlerMap);
    }

    private Callable<Boolean> getMetricsTask() {
        return jmxMetricsHash ? new HashMessageRecvInitializeTask(request, response, handlerMap) : new MessageRecvInitializeTask(request, response, handlerMap);
    }
}

