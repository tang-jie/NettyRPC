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

import com.newlandframework.rpc.core.AbilityDetail;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcAbilityDetailProviderTest.java
 * @description:RpcAbilityDetailProviderTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/5/2
 */
public class RpcAbilityDetailProviderTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        AbilityDetail provider = (AbilityDetail) context.getBean("ability");

        StringBuilder ability = provider.listAbilityDetail(false);

        System.out.println(ability);

        context.destroy();
    }
}

