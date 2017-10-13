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
package com.newlandframework.rpc.event;

import com.newlandframework.rpc.jmx.ModuleMetricsHandler;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:InvokeEventBusFacade.java
 * @description:InvokeEventBusFacade功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class InvokeEventBusFacade {
    private static Map<AbstractInvokeEventBus.ModuleEvent, AbstractInvokeEventBus> enumMap = new EnumMap<AbstractInvokeEventBus.ModuleEvent, AbstractInvokeEventBus>(AbstractInvokeEventBus.ModuleEvent.class);

    static {
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_EVENT, new InvokeEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_SUCC_EVENT, new InvokeSuccEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_FAIL_EVENT, new InvokeFailEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_FILTER_EVENT, new InvokeFilterEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_TIMESPAN_EVENT, new InvokeTimeSpanEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_MAX_TIMESPAN_EVENT, new InvokeMaxTimeSpanEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_MIN_TIMESPAN_EVENT, new InvokeMinTimeSpanEvent());
        enumMap.put(AbstractInvokeEventBus.ModuleEvent.INVOKE_FAIL_STACKTRACE_EVENT, new InvokeFailStackTraceEvent());
    }

    public InvokeEventBusFacade(ModuleMetricsHandler handler, String moduleName, String methodName) {
        for (AbstractInvokeEventBus event : enumMap.values()) {
            event.setHandler(handler);
            event.setModuleName(moduleName);
            event.setMethodName(methodName);
        }
    }

    public AbstractInvokeEventBus fetchEvent(AbstractInvokeEventBus.ModuleEvent event) {
        if (enumMap.containsKey(event)) {
            return enumMap.get(event);
        } else {
            return null;
        }
    }
}

