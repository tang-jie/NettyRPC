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

import com.newlandframework.rpc.exception.InvokeTimeoutException;
import com.newlandframework.rpc.services.PersonManage;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:PojoTimeoutCallTest.java
 * @description:PojoTimeoutCallTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/28
 */
public class PojoTimeoutCallTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        PersonManage manage = (PersonManage) context.getBean("personManage");

        // TODO: 2017/10/28 by tangjie
        //NettyRPC default timeout is 30s.you can define it by nettyrpc.default.msg.timeout environment variable.
        //if rpc call timeout,NettyRPC can throw InvokeTimeoutException.
        try {
            long timeout = 32L;
            manage.query(timeout);
        } catch (InvokeTimeoutException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            context.destroy();
        }
    }
}

