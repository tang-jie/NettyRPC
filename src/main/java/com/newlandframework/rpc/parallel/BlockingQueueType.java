/**
 * Copyright (C) 2016 Newland Group Holding Limited
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
 * @filename:BlockingQueueType.java
 * @description:BlockingQueueType功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public enum BlockingQueueType {
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"),
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"),
    SYNCHRONOUS_QUEUE("SynchronousQueue");

    private String value;

    private BlockingQueueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BlockingQueueType fromString(String value) {
        for (BlockingQueueType type : BlockingQueueType.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    public String toString() {
        return value;
    }
}

