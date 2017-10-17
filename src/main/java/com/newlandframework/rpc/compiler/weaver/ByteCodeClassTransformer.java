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
package com.newlandframework.rpc.compiler.weaver;

import com.newlandframework.rpc.compiler.invoke.ObjectInvoker;
import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.exception.CreateProxyException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ByteCodeClassTransformer.java
 * @description:ByteCodeClassTransformer功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/8/30
 */
public class ByteCodeClassTransformer extends AbstractClassTransformer implements Opcodes {
    private static final AtomicLong CLASS_NUMBER = new AtomicLong(0);
    private static final String CLASSNAME_PREFIX = "ASMPROXY_";
    private static final String HANDLER_NAME = "__handler";
    private static final Type INVOKER_TYPE = Type.getType(ObjectInvoker.class);

    @Override
    public Class<?> transform(ClassLoader classLoader, Class<?>... proxyClasses) {
        Class<?> superclass = ReflectionUtils.getParentClass(proxyClasses);
        String proxyName = CLASSNAME_PREFIX + CLASS_NUMBER.incrementAndGet();
        Method[] implementationMethods = super.findImplementationMethods(proxyClasses);
        Class<?>[] interfaces = ReflectionUtils.filterInterfaces(proxyClasses);
        String classFileName = proxyName.replace('.', '/');

        try {
            byte[] proxyBytes = generate(superclass, classFileName, implementationMethods, interfaces);
            return loadClass(classLoader, proxyName, proxyBytes);
        } catch (final Exception e) {
            throw new CreateProxyException(e);
        }
    }

    private byte[] generate(Class<?> classToProxy, String proxyName, Method[] methods, Class<?>... interfaces) throws CreateProxyException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        Type proxyType = Type.getObjectType(proxyName);

        String[] interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceNames[i] = Type.getType(interfaces[i]).getInternalName();
        }

        Type superType = Type.getType(classToProxy);

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, proxyType.getInternalName(), null, superType.getInternalName(), interfaceNames);

        cw.visitField(ACC_FINAL + ACC_PRIVATE, HANDLER_NAME, INVOKER_TYPE.getDescriptor(), null, null).visitEnd();

        initialize(cw, proxyType, superType);

        for (final Method method : methods) {
            transformMethod(cw, method, proxyType, HANDLER_NAME);
        }

        return cw.toByteArray();
    }

    // FIXME: 2017/8/30 by tangjie
    // 字节码中的init方法必须先行初始化。
    // clinit就暂时不考虑了
    private void initialize(ClassWriter cw, Type proxyType, Type superType) {
        GeneratorAdapter adapter =
                new GeneratorAdapter(ACC_PUBLIC, new org.objectweb.asm.commons.Method("<init>", Type.VOID_TYPE,
                        new Type[]{INVOKER_TYPE}), null, null, cw);
        adapter.loadThis();
        adapter.invokeConstructor(superType, org.objectweb.asm.commons.Method.getMethod("void <init> ()"));
        adapter.loadThis();
        adapter.loadArg(0);
        adapter.putField(proxyType, HANDLER_NAME, INVOKER_TYPE);
        adapter.returnValue();
        adapter.endMethod();
    }

    private Type[] getTypes(Class<?>... src) {
        Type[] result = new Type[src.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Type.getType(src[i]);
        }

        return result;
    }

    // FIXME: 2017/8/30 by tangjie
    // 本来想用cglib来实现对字节码的控制，但是考虑到性能问题，决定采用偏向底层的ASM对JVM的字节码进行渲染织入增强。
    // 其中获取方法签名通过反射方式取得，虽然性能上可能有所损失，但是编码方式比较简洁，不会出现大量的ASM堆栈操作的API序列。
    private void transformMethod(ClassWriter cw, Method method, Type proxyType, String handlerName) throws CreateProxyException {
        int access = (ACC_PUBLIC | ACC_PROTECTED) & method.getModifiers();
        org.objectweb.asm.commons.Method m = org.objectweb.asm.commons.Method.getMethod(method);
        GeneratorAdapter adapter = new GeneratorAdapter(access, m, null, getTypes(method.getExceptionTypes()), cw);

        //方法签名入栈
        adapter.push(Type.getType(method.getDeclaringClass()));
        adapter.push(method.getName());
        adapter.push(Type.getArgumentTypes(method).length);

        //创建Class对象
        Type classType = Type.getType(Class.class);
        adapter.newArray(classType);

        //获取方法参数列表
        for (int i = 0; i < Type.getArgumentTypes(method).length; i++) {
            //从方法堆栈顶复制一份参数类型
            adapter.dup();

            //把参数索引入栈
            adapter.push(i);
            adapter.push(Type.getArgumentTypes(method)[i]);
            adapter.arrayStore(classType);
        }

        //调用getDeclaredMethod方法
        adapter.invokeVirtual(classType,
                org.objectweb.asm.commons.Method.getMethod("java.lang.reflect.Method getDeclaredMethod(String, Class[])"));

        adapter.loadThis();
        adapter.getField(proxyType, handlerName, INVOKER_TYPE);

        //偏移堆栈指针
        adapter.swap();

        adapter.loadThis();

        //偏移堆栈指针
        adapter.swap();

        //获取方法的参数取值列表
        adapter.push(Type.getArgumentTypes(method).length);
        Type objectType = Type.getType(Object.class);
        adapter.newArray(objectType);

        for (int i = 0; i < Type.getArgumentTypes(method).length; i++) {
            //从方法堆栈顶复制一份参数类型
            adapter.dup();

            adapter.push(i);

            adapter.loadArg(i);
            adapter.valueOf(Type.getArgumentTypes(method)[i]);
            adapter.arrayStore(objectType);
        }

        //调用方法
        adapter.invokeInterface(INVOKER_TYPE,
                org.objectweb.asm.commons.Method.getMethod("Object invoke(Object, java.lang.reflect.Method, Object[])"));

        //方法的返回值拆箱
        adapter.unbox(Type.getReturnType(method));

        adapter.returnValue();

        adapter.endMethod();
    }

    // FIXME: 2017/8/31 by tangjie
    // 这里可以考虑引入独立的类加载器，对于织入增强的字节码重新热加载到虚拟机。
    // 先这么写，后续再考虑优化。
    private Class<?> loadClass(ClassLoader loader, String className, byte[] b) {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);

            boolean accessible = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            try {
                return (Class<?>) method.invoke(loader, className, b, Integer.valueOf(0), Integer.valueOf(b.length));
            } finally {
                if (!accessible) {
                    method.setAccessible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

