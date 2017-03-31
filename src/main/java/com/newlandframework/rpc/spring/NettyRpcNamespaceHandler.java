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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:NettyRpcNamespaceHandler.java
 * @description:NettyRpcNamespaceHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class NettyRpcNamespaceHandler extends NamespaceHandlerSupport {
    static {
        Resource resource = new ClassPathResource("NettyRPC-logo.txt");
        if (resource.exists()) {
            try {
                Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
                String text = CharStreams.toString(reader);
                System.out.println(text);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("");
            System.out.println(" _      _____ _____ _____ ___  _ ____  ____  ____ ");
            System.out.println("/ \\  /|/  __//__ __Y__ __\\\\  \\///  __\\/  __\\/   _\\");
            System.out.println("| |\\ |||  \\    / \\   / \\   \\  / |  \\/||  \\/||  /  ");
            System.out.println("| | \\|||  /_   | |   | |   / /  |    /|  __/|  \\_ ");
            System.out.println("\\_/  \\|\\____\\  \\_/   \\_/  /_/   \\_/\\_\\\\_/   \\____/");
            System.out.println("[NettyRPC 2.0,Build 2016/10/7,Author:tangjie http://www.cnblogs.com/jietang/]");
            System.out.println("");
        }
    }

    public void init() {
        registerBeanDefinitionParser("service", new NettyRpcServiceParser());
        registerBeanDefinitionParser("registry", new NettyRpcRegisteryParser());
        registerBeanDefinitionParser("reference", new NettyRpcReferenceParser());
    }
}

