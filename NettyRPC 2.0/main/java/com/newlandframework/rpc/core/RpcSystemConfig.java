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
package com.newlandframework.rpc.core;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcSystemConfig.java
 * @description:RpcSystemConfig功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class RpcSystemConfig {
    public static final String SystemPropertyThreadPoolRejectedPolicyAttr = "com.newlandframework.rpc.parallel.rejected.policy";
    public static final String SystemPropertyThreadPoolQueueNameAttr = "com.newlandframework.rpc.parallel.queue";
    public static final int PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

    private static boolean monitorServerSupport = false;

    public static boolean isMonitorServerSupport() {
        return monitorServerSupport;
    }

    public static void setMonitorServerSupport(boolean jmxSupport) {
        monitorServerSupport = jmxSupport;
    }
}

