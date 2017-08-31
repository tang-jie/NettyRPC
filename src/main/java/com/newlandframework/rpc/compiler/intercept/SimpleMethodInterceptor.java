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
package com.newlandframework.rpc.compiler.intercept;

import org.apache.commons.lang3.StringUtils;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:SimpleMethodInterceptor.java
 * @description:SimpleMethodInterceptor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/8/30
 */
public class SimpleMethodInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println(StringUtils.center("[SimpleMethodInterceptor##intercept]", 48, "*"));
        // FIXME: 2017/8/30 by tangjie
        // 对于RPC客户端过来的反射请求，不能无限制地在服务端直接运行，这样可能有安全隐患！
        // 这里可以对一些敏感的关键字进行拦截处理
        // 这里可以加入你自己的业务逻辑代码。
        // TODO: your intercept logic here

        return invocation.proceed();
    }
}

