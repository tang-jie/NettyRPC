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
package com.newlandframework.test;

import com.google.common.io.CharStreams;
import com.newlandframework.rpc.compiler.AccessAdaptive;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcServerAccessTest.java
 * @description:RpcServerAccessTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/30
 */
public class RpcServerAccessTest {

    public static void main(String[] args) {
        try {
            DefaultResourceLoader resource = new DefaultResourceLoader();
            Reader input = new InputStreamReader(resource.getResource("AccessProvider.tpl").getInputStream(), "UTF-8");
            String javaSource = CharStreams.toString(input);

            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

            AccessAdaptive provider = (AccessAdaptive) context.getBean("access");

            String result = (String) provider.invoke(javaSource, "getRpcServerTime", new Object[]{new String("XiaoHaoBaby")});
            System.out.println(result);

            provider.invoke(javaSource, "sayHello", new Object[0]);

            input.close();
            context.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

