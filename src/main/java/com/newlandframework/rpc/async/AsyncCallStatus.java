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
package com.newlandframework.rpc.async;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AsyncCallStatus.java
 * @description:AsyncCallStatus功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class AsyncCallStatus {
    private long startTime;
    private long elapseTime;
    private CallStatus status;

    public AsyncCallStatus(long startTime, long elapseTime, CallStatus status) {
        this.startTime = startTime;
        this.elapseTime = elapseTime;
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getElapseTime() {
        return elapseTime;
    }

    public void setElapseTime(long elapseTime) {
        this.elapseTime = elapseTime;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public String toString() {
        return "AsyncLoadStatus [status=" + status + ", startTime=" + startTime + ", elapseTime=" + elapseTime + "]";
    }
}

