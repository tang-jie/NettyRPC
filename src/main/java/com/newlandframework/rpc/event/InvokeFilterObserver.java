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

import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;

import java.util.Observable;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:InvokeFilterObserver.java
 * @description:InvokeFilterObserver功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class InvokeFilterObserver extends AbstractInvokeObserver {

    public InvokeFilterObserver(InvokeEventBusFacade facade, ModuleMetricsVisitor visitor) {
        super(facade, visitor);
    }

    @Override
    public void update(Observable o, Object arg) {
        if ((AbstractInvokeEventBus.ModuleEvent) arg == AbstractInvokeEventBus.ModuleEvent.INVOKE_FILTER_EVENT) {
            super.getFacade().fetchEvent(AbstractInvokeEventBus.ModuleEvent.INVOKE_FILTER_EVENT).notify(super.getVisitor().getInvokeFilterCount(), super.getVisitor().incrementInvokeFilterCount());
        }
    }
}
