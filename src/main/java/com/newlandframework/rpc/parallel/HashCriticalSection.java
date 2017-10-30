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
package com.newlandframework.rpc.parallel;

import com.newlandframework.rpc.core.RpcSystemConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:HashCriticalSection.java
 * @description:HashCriticalSection功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/26
 */
public class HashCriticalSection {
    private static Integer partition = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS;
    private final Map<Integer, Semaphore> criticalSectionMap = new ConcurrentHashMap<Integer, Semaphore>();
    public final static long BASIC = 0xcbf29ce484222325L;
    public final static long PRIME = 0x100000001b3L;

    public HashCriticalSection() {
        boolean fair = RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_LOCK_FAIR == 1;
        init(null, fair);
    }

    public HashCriticalSection(Integer counts, boolean fair) {
        init(counts, fair);
    }

    public static int hash(String key) {
        return Math.abs((int) (fnv1a64(key) % partition));
    }

    public static long fnv1a64(String key) {
        long hashCode = BASIC;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);

            if (ch >= 'A' && ch <= 'Z') {
                ch = (char) (ch + 32);
            }

            hashCode ^= ch;
            hashCode *= PRIME;
        }

        return hashCode;
    }

    private void init(Integer counts, boolean fair) {
        if (counts != null) {
            partition = counts;
        }
        for (int i = 0; i < partition; i++) {
            criticalSectionMap.put(i, new Semaphore(1, fair));
        }
    }

    public void enter(String key) {
        int hashKey = hash(key);
        Semaphore semaphore = criticalSectionMap.get(hashKey);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit(String key) {
        int hashKey = hash(key);
        Semaphore semaphore = criticalSectionMap.get(hashKey);
        semaphore.release();
    }

    public void enter(int hashKey) {
        Semaphore semaphore = criticalSectionMap.get(hashKey);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit(int hashKey) {
        Semaphore semaphore = criticalSectionMap.get(hashKey);
        semaphore.release();
    }
}

