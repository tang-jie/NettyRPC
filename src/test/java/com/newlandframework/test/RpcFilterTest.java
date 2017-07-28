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

import com.newlandframework.rpc.exception.RejectResponeException;
import com.newlandframework.rpc.services.Cache;
import com.newlandframework.rpc.services.Store;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcFilterTest.java
 * @description:RpcFilterTest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/7/28
 */
public class RpcFilterTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-client.xml");

        Cache cache = (Cache) context.getBean("cache");

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                cache.put(obj, obj);
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                System.out.println((String) cache.get(obj));
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        Store store = (Store) context.getBean("store");

        for (int i = 0; i < 100; i++) {
            String obj = String.valueOf(i);
            try {
                store.save(obj);
            } catch (RejectResponeException ex) {
                System.out.println("trace:" + ex.getMessage());
            }
        }

        context.destroy();
    }
}

