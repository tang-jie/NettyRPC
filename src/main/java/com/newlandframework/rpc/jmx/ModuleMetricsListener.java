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
package com.newlandframework.rpc.jmx;

import com.newlandframework.rpc.event.AbstractInvokeEventBus;

import javax.management.AttributeChangeNotification;
import javax.management.JMException;
import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleMetricsListener.java
 * @description:ModuleMetricsListener功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class ModuleMetricsListener implements NotificationListener {
    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (!(notification instanceof AttributeChangeNotification)) {
            return;
        }

        AttributeChangeNotification acn = (AttributeChangeNotification) notification;
        AbstractInvokeEventBus.ModuleEvent event = Enum.valueOf(AbstractInvokeEventBus.ModuleEvent.class, acn.getAttributeType());
        ModuleMetricsVisitor visitor = ModuleMetricsHandler.getInstance().visit(acn.getMessage(), acn.getAttributeName());

        switch (event) {
            case INVOKE_EVENT:
                visitor.setInvokeCount(((Long) acn.getNewValue()).longValue());
                break;
            case INVOKE_SUCC_EVENT:
                visitor.setInvokeSuccCount(((Long) acn.getNewValue()).longValue());
                break;
            case INVOKE_FAIL_EVENT:
                visitor.setInvokeFailCount(((Long) acn.getNewValue()).longValue());
                break;
            case INVOKE_FILTER_EVENT:
                visitor.setInvokeFilterCount(((Long) acn.getNewValue()).longValue());
                break;
            case INVOKE_TIMESPAN_EVENT:
                visitor.setInvokeTimespan(((Long) acn.getNewValue()).longValue());
                visitor.getHistogram().record(((Long) acn.getNewValue()).longValue());
                break;
            case INVOKE_MAX_TIMESPAN_EVENT:
                if ((Long) acn.getNewValue() > (Long) acn.getOldValue()) {
                    visitor.setInvokeMaxTimespan(((Long) acn.getNewValue()).longValue());
                }
                break;
            case INVOKE_MIN_TIMESPAN_EVENT:
                if ((Long) acn.getNewValue() < (Long) acn.getOldValue()) {
                    visitor.setInvokeMinTimespan(((Long) acn.getNewValue()).longValue());
                }
                break;
            case INVOKE_FAIL_STACKTRACE_EVENT:
                try {
                    visitor.setLastStackTrace((Exception) acn.getNewValue());
                    visitor.buildErrorCompositeData((Exception) acn.getNewValue());
                } catch (JMException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}

