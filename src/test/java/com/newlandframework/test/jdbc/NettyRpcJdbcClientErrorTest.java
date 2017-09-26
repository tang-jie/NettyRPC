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
package com.newlandframework.test.jdbc;

import com.newlandframework.rpc.exception.InvokeModuleException;
import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:NettyRpcJdbcClientErrorTest.java
 * @description:NettyRpcJdbcClientErrorTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/9/25
 */
public class NettyRpcJdbcClientErrorTest {
    // FIXME: 2017/9/25 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-jdbc-client.xml");

        JdbcPersonManage manage = (JdbcPersonManage) context.getBean("personManageJdbc");

        //验证RPC调用服务端执行失败的情况！
        Person p = new Person();
        p.setId(20150811);
        p.setName("XiaoHaoBaby");
        p.setAge(999999999);

        try {
            int result = manage.save(p);
            System.out.println("call pojo rpc result:" + result);
        } catch (InvokeModuleException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            context.destroy();
        }
    }
}

