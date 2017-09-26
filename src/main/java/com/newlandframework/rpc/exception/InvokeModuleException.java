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
package com.newlandframework.rpc.exception;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:InvokeModuleException.java
 * @description:InvokeModuleException功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/9/26
 */
public class InvokeModuleException extends RuntimeException {
    public InvokeModuleException() {
        super();
    }

    public InvokeModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeModuleException(String message) {
        super(message);
    }

    public InvokeModuleException(Throwable cause) {
        super(cause);
    }
}

