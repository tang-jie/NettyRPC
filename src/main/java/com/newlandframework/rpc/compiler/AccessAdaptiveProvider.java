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
package com.newlandframework.rpc.compiler;

import com.google.common.io.Files;
import com.newlandframework.rpc.compiler.intercept.SimpleMethodInterceptor;
import com.newlandframework.rpc.core.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AccessAdaptiveProvider.java
 * @description:AccessAdaptiveProvider功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/30
 */
public class AccessAdaptiveProvider extends AbstractAccessAdaptive implements AccessAdaptive {
    @Override
    protected Class<?> doCompile(String clsName, String javaSource) throws Throwable {
        File tempFileLocation = Files.createTempDir();
        compiler = new NativeCompiler(tempFileLocation);
        Class type = compiler.compile(clsName, javaSource);
        tempFileLocation.deleteOnExit();
        return type;
    }

    @Override
    public Object invoke(String javaSource, String method, Object[] args) {
        if (StringUtils.isEmpty(javaSource) || StringUtils.isEmpty(method)) {
            return null;
        } else {
            try {
                Class type = compile(javaSource, Thread.currentThread().getContextClassLoader());
                Object object = ReflectionUtils.newInstance(type);
                Thread.currentThread().getContextClassLoader().loadClass(type.getName());
                Object proxy = getFactory().createProxy(object, new SimpleMethodInterceptor(), new Class[]{type});
                return MethodUtils.invokeMethod(proxy, method, args);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
