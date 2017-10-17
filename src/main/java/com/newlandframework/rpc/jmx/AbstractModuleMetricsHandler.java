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

import com.newlandframework.rpc.parallel.SemaphoreWrapper;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AbstractModuleMetricsHandler.java
 * @description:AbstractModuleMetricsHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/13
 */
public abstract class AbstractModuleMetricsHandler extends NotificationBroadcasterSupport implements ModuleMetricsVisitorMXBean {
    public final static String MBEAN_NAME = "com.newlandframework.rpc:type=ModuleMetricsHandler";
    public final static int MODULE_METRICS_JMX_PORT = 1098;
    protected String moduleMetricsJmxUrl = "";
    protected Semaphore semaphore = new Semaphore(0);
    protected SemaphoreWrapper semaphoreWrapper = new SemaphoreWrapper(semaphore);
    protected List<ModuleMetricsVisitor> visitorList = new CopyOnWriteArrayList<ModuleMetricsVisitor>();
    protected static String startTime;
    protected ModuleMetricsListener listener = new ModuleMetricsListener();
    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();

    public AbstractModuleMetricsHandler() {

    }

    public ModuleMetricsVisitor visit(String moduleName, String methodName) {
        try {
            enter();
            return visitCriticalSection(moduleName, methodName);
        } finally {
            exit();
        }
    }

    @Override
    public List<ModuleMetricsVisitor> getModuleMetricsVisitor() {
        return visitorList;
    }

    @Override
    public void addModuleMetricsVisitor(ModuleMetricsVisitor visitor) {
        visitorList.add(visitor);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[]{
                AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        String name = AttributeChangeNotification.class.getName();
        String description = "the event send from NettyRPC server!";
        MBeanNotificationInfo info =
                new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[]{info};
    }

    public final static String getStartTime() {
        if (startTime == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = format.format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
        }
        return startTime;
    }

    protected void enter() {
        Thread current = Thread.currentThread();
        waiters.add(current);

        while (waiters.peek() != current || !locked.compareAndSet(false, true)) {
            LockSupport.park(ModuleMetricsVisitor.class);
        }

        waiters.remove();
    }

    protected void exit() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }

    protected abstract ModuleMetricsVisitor visitCriticalSection(String moduleName, String methodName);
}

