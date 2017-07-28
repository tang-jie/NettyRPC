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
package com.newlandframework.rpc.filter;

import java.lang.reflect.Method;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:Filter.java
 * @description:Filter功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/7/27
 */
public interface Filter {
    // TODO: 2017/7/27 by tangjie
    // If returns false, indicates that the RPC request method is intercept.
    boolean before(Method method, Object processor, Object[] requestObjects);

    // If filter's before function returns false, filter's after function will not be called.
    void after(Method method, Object processor, Object[] requestObjects);
}

