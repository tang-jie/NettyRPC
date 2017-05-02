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
package com.newlandframework.rpc.spring;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.jmx.ThreadPoolMonitorProvider;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;
import com.newlandframework.rpc.netty.MessageRecvExecutor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:NettyRpcRegistery.java
 * @description:NettyRpcRegistery功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class NettyRpcRegistery implements InitializingBean, DisposableBean {
    private String ipAddr;
    private String protocol;
    private String echoApiPort;
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    public void destroy() throws Exception {
        MessageRecvExecutor.getInstance().stop();
    }

    public void afterPropertiesSet() throws Exception {
        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(ipAddr);
        ref.setEchoApiPort(Integer.parseInt(echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, protocol));

        if (RpcSystemConfig.isMonitorServerSupport()) {
            context.register(ThreadPoolMonitorProvider.class);
            context.refresh();
        }

        ref.start();
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(String echoApiPort) {
        this.echoApiPort = echoApiPort;
    }
}


