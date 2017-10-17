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

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:SemaphoreWrapperFactory.java
 * @description:SemaphoreWrapperFactory功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/13
 */
public class SemaphoreWrapperFactory extends SemaphoreWrapper {
    private static final SemaphoreWrapperFactory INSTANCE = new SemaphoreWrapperFactory();

    public static SemaphoreWrapperFactory getInstance() {
        return INSTANCE;
    }

    private SemaphoreWrapperFactory() {
        super();
    }

    @Override
    public void acquire() {
        if (semaphore != null) {
            try {
                while (true) {
                    boolean result = released.get();
                    if (released.compareAndSet(result, true)) {
                        semaphore.acquire();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        if (semaphore != null) {
            while (true) {
                boolean result = released.get();
                if (released.compareAndSet(result, false)) {
                    semaphore.release();
                    break;
                }
            }
        }
    }
}

