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
package com.newlandframework.rpc.jmx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;
import com.newlandframework.rpc.netty.MessageRecvExecutor;
import org.apache.commons.lang3.StringUtils;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.InstanceNotFoundException;
import java.io.IOException;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ThreadPoolMonitorProvider.java
 * @description:ThreadPoolMonitorProvider功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/13
 */

@Configuration
@EnableMBeanExport
@ComponentScan("com.newlandframework.rpc.jmx")
public class ThreadPoolMonitorProvider {
    public final static String DELIMITER = ":";
    public final static String jmxPoolSizeMethod = "setPoolSize";
    public final static String jmxActiveCountMethod = "setActiveCount";
    public final static String jmxCorePoolSizeMethod = "setCorePoolSize";
    public final static String jmxMaximumPoolSizeMethod = "setMaximumPoolSize";
    public final static String jmxLargestPoolSizeMethod = "setLargestPoolSize";
    public final static String jmxTaskCountMethod = "setTaskCount";
    public final static String jmxCompletedTaskCountMethod = "setCompletedTaskCount";
    public static String url;

    @Bean
    public ThreadPoolStatus threadPoolStatus() {
        return new ThreadPoolStatus();
    }

    @Bean
    public MBeanServerFactoryBean mbeanServer() {
        return new MBeanServerFactoryBean();
    }

    @Bean
    public RmiRegistryFactoryBean registry() {
        return new RmiRegistryFactoryBean();
    }

    @Bean
    @DependsOn("registry")
    public ConnectorServerFactoryBean connectorServer() throws MalformedObjectNameException {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        String ipAddr = StringUtils.isNotEmpty(ref.getServerAddress()) ? StringUtils.substringBeforeLast(ref.getServerAddress(), DELIMITER) : "localhost";
        url = "service:jmx:rmi://" + ipAddr + "/jndi/rmi://" + ipAddr + ":1099/nettyrpcstatus";
        System.out.println("NettyRPC JMX MonitorURL : [" + url + "]");
        ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=rmi");
        connectorServerFactoryBean.setServiceUrl(url);
        return connectorServerFactoryBean;
    }

    public static void monitor(ThreadPoolStatus status) throws IOException, MalformedObjectNameException, ReflectionException, MBeanException, InstanceNotFoundException {
        MBeanServerConnectionFactoryBean mBeanServerConnectionFactoryBean = new MBeanServerConnectionFactoryBean();
        mBeanServerConnectionFactoryBean.setServiceUrl(url);
        mBeanServerConnectionFactoryBean.afterPropertiesSet();
        MBeanServerConnection connection = mBeanServerConnectionFactoryBean.getObject();
        ObjectName objectName = new ObjectName("com.newlandframework.rpc.jmx:name=threadPoolStatus,type=ThreadPoolStatus");

        connection.invoke(objectName, jmxPoolSizeMethod, new Object[]{status.getPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxActiveCountMethod, new Object[]{status.getActiveCount()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxCorePoolSizeMethod, new Object[]{status.getCorePoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxMaximumPoolSizeMethod, new Object[]{status.getMaximumPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxLargestPoolSizeMethod, new Object[]{status.getLargestPoolSize()}, new String[]{int.class.getName()});
        connection.invoke(objectName, jmxTaskCountMethod, new Object[]{status.getTaskCount()}, new String[]{long.class.getName()});
        connection.invoke(objectName, jmxCompletedTaskCountMethod, new Object[]{status.getCompletedTaskCount()}, new String[]{long.class.getName()});
    }
}

