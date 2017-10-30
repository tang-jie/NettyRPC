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

import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:NettyRpcJdbcClientTest.java
 * @description:NettyRpcJdbcClientTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/9/25
 */
public class NettyRpcJdbcClientTest {
    // FIXME: 2017/9/25 确保先启动NettyRPC服务端应用:NettyRpcJdbcServerTest，再运行NettyRpcJdbcClientTest、NettyRpcJdbcClientErrorTest！
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-jdbc-client.xml");

        JdbcPersonManage manage = (JdbcPersonManage) context.getBean("personManageJdbc");

        try {
            Person p = new Person();
            p.setId(1);
            p.setName("小好");
            p.setAge(2);
            p.setBirthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-08-11 16:28:00"));
            int result = manage.save(p);
            manage.query(p);
            System.out.println("call pojo rpc result:" + result);

            System.out.println("---------------------------------------------");

            List<Person> list = manage.query();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            context.destroy();
        }
    }
}

