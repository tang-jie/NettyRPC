/**
 * Copyright (C) 2018 Newland Group Holding Limited
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
package com.newlandframework.rpc.listener.support;

import com.newlandframework.rpc.core.ModuleProvider;
import com.newlandframework.rpc.listener.ModuleListener;
import com.newlandframework.rpc.model.MessageRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleListenerAdapter.java
 * @description:ModuleListenerAdapter功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/1
 */
public class ModuleListenerAdapter implements ModuleListener {
    @Override
    public void exported(ModuleProvider<?> provider, MessageRequest request) {
        System.out.println(StringUtils.center("[ModuleListenerAdapter##exported]", 48, "*"));
    }

    @Override
    public void unExported(ModuleProvider<?> provider, MessageRequest request) {
        System.out.println(StringUtils.center("[ModuleListenerAdapter##unExported]", 48, "*"));
    }
}

