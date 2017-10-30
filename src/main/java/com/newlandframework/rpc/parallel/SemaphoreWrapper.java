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

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:SemaphoreWrapper.java
 * @description:SemaphoreWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class SemaphoreWrapper {
    protected final AtomicBoolean released = new AtomicBoolean(false);
    protected Semaphore semaphore;

    public SemaphoreWrapper() {
        semaphore = new Semaphore(1);
    }

    public SemaphoreWrapper(int permits) {
        semaphore = new Semaphore(permits);
    }

    public SemaphoreWrapper(int permits, boolean fair) {
        semaphore = new Semaphore(permits, fair);
    }

    public SemaphoreWrapper(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public void release() {
        if (this.semaphore != null) {
            if (this.released.compareAndSet(false, true)) {
                this.semaphore.release();
            }
        }
    }

    public void acquire() {
        if (this.semaphore != null) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public boolean isRelease() {
        return released.get();
    }
}

