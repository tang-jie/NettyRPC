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

import com.newlandframework.rpc.netty.MessageRecvExecutor;
import com.newlandframework.rpc.parallel.AbstractDaemonThread;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.lang3.StringUtils;

import javax.management.*;
import javax.management.remote.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Iterator;

import static com.newlandframework.rpc.core.RpcSystemConfig.DELIMITER;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleMetricsHandler.java
 * @description:ModuleMetricsHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class ModuleMetricsHandler extends AbstractModuleMetricsHandler {
    private static final ModuleMetricsHandler INSTANCE = new ModuleMetricsHandler();
    private MBeanServerConnection connection;

    public static ModuleMetricsHandler getInstance() {
        return INSTANCE;
    }

    private ModuleMetricsHandler() {
        super();
    }

    @Override
    protected ModuleMetricsVisitor visitCriticalSection(String moduleName, String methodName) {
        final String method = methodName.trim();
        final String module = moduleName.trim();

        //FIXME: 2017/10/13 by tangjie
        //JMX度量临界区要注意线程间的并发竞争,否则会统计数据失真
        Iterator iterator = new FilterIterator(visitorList.iterator(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                String statModuleName = ((ModuleMetricsVisitor) object).getModuleName();
                String statMethodName = ((ModuleMetricsVisitor) object).getMethodName();
                return statModuleName.compareTo(module) == 0 && statMethodName.compareTo(method) == 0;
            }
        });

        ModuleMetricsVisitor visitor = null;
        while (iterator.hasNext()) {
            visitor = (ModuleMetricsVisitor) iterator.next();
            break;
        }

        if (visitor != null) {
            return visitor;
        } else {
            visitor = new ModuleMetricsVisitor(module, method);
            addModuleMetricsVisitor(visitor);
            return visitor;
        }
    }

    public void start() {
        new AbstractDaemonThread() {
            @Override
            public String getDeamonThreadName() {
                return ModuleMetricsHandler.class.getSimpleName();
            }

            @Override
            public void run() {
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                try {
                    LocateRegistry.createRegistry(MODULE_METRICS_JMX_PORT);
                    MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
                    String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ? StringUtils.substringBeforeLast(ref.getServerAddress(), DELIMITER) : "localhost";
                    moduleMetricsJmxUrl = "service:jmx:rmi:///jndi/rmi://" + ipAddr + ":" + MODULE_METRICS_JMX_PORT + "/NettyRPCServer";
                    JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
                    JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);

                    ObjectName name = new ObjectName(MBEAN_NAME);

                    mbs.registerMBean(ModuleMetricsHandler.this, name);
                    mbs.addNotificationListener(name, listener, null, null);
                    cs.start();

                    semaphoreWrapper.release();

                    System.out.printf("NettyRPC JMX server is start success!\njmx-url:[ %s ]\n\n", moduleMetricsJmxUrl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MBeanRegistrationException e) {
                    e.printStackTrace();
                } catch (InstanceAlreadyExistsException e) {
                    e.printStackTrace();
                } catch (NotCompliantMBeanException e) {
                    e.printStackTrace();
                } catch (MalformedObjectNameException e) {
                    e.printStackTrace();
                } catch (InstanceNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void stop() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName(MBEAN_NAME);
            mbs.unregisterMBean(name);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

    public MBeanServerConnection connect() {
        try {
            if (!semaphoreWrapper.isRelease()) {
                semaphoreWrapper.acquire();
            }

            JMXServiceURL url = new JMXServiceURL(moduleMetricsJmxUrl);
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            connection = jmxc.getMBeanServerConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }
}

