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
 * @filename:AbstractDaemonThread.java
 * @description:AbstractDaemonThread功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public abstract class AbstractDaemonThread implements Runnable {
    protected final Thread thread;
    private static final long JOIN_TIME = 90 * 1000L;
    protected volatile boolean hasNotified = false;
    protected volatile boolean stoped = false;

    public AbstractDaemonThread() {
        this.thread = new Thread(this, this.getDeamonThreadName());
    }

    public abstract String getDeamonThreadName();

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.shutdown(false);
    }

    public void stop() {
        this.stop(false);
    }

    public void makeStop() {
        this.stoped = true;
    }


    public void stop(final boolean interrupt) {
        this.stoped = true;
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }

        if (interrupt) {
            this.thread.interrupt();
        }
    }


    public void shutdown(final boolean interrupt) {
        this.stoped = true;
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }

        try {
            if (interrupt) {
                this.thread.interrupt();
            }

            if (!this.thread.isDaemon()) {
                this.thread.join(this.getJoinTime());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void wakeup() {
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }
    }

    protected void waitForRunning(long interval) {
        synchronized (this) {
            if (this.hasNotified) {
                this.hasNotified = false;
                this.onWaitEnd();
                return;
            }

            try {
                this.wait(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.hasNotified = false;
                this.onWaitEnd();
            }
        }
    }

    protected void onWaitEnd() {
    }

    public boolean isStoped() {
        return stoped;
    }

    public long getJoinTime() {
        return JOIN_TIME;
    }
}

