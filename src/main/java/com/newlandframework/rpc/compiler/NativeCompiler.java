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

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:NativeCompiler.java
 * @description:NativeCompiler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/30
 */
public class NativeCompiler implements Closeable {
    private final File tempFolder;
    private final URLClassLoader classLoader;

    public NativeCompiler(File tempFolder) {
        this.tempFolder = tempFolder;
        this.classLoader = createClassLoader(tempFolder);
    }

    private static URLClassLoader createClassLoader(File tempFolder) {
        try {
            URL[] urls = {tempFolder.toURI().toURL()};
            return new URLClassLoader(urls);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public Class<?> compile(String className, String code) {
        try {
            JavaFileObject sourceFile = new StringJavaFileObject(className, code);
            compileClass(sourceFile);
            return classLoader.loadClass(className);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private void compileClass(JavaFileObject sourceFile) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = null;

        try {
            fileManager = compiler.getStandardFileManager(collector, Locale.ROOT, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFolder));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Arrays.asList(sourceFile));
            task.call();
        } finally {
            fileManager.close();
        }
    }

    @Override
    public void close() {
        try {
            classLoader.close();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}

