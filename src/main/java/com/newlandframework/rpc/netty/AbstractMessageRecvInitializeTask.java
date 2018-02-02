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
package com.newlandframework.rpc.netty;

import com.newlandframework.rpc.core.Modular;
import com.newlandframework.rpc.core.ModuleInvoker;
import com.newlandframework.rpc.core.ModuleProvider;
import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.spring.BeanFactoryUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AbstractMessageRecvInitializeTask.java
 * @description:AbstractMessageRecvInitializeTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/13
 */
public abstract class AbstractMessageRecvInitializeTask implements Callable<Boolean> {
    protected MessageRequest request = null;
    protected MessageResponse response = null;
    protected Map<String, Object> handlerMap = null;
    protected static final String METHOD_MAPPED_NAME = "invoke";
    protected boolean returnNotNull = true;
    protected long invokeTimespan;
    protected Modular modular = BeanFactoryUtils.getBean("modular");

    public AbstractMessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        try {
            acquire();
            response.setMessageId(request.getMessageId());
            injectInvoke();
            Object result = reflect(request);
            boolean isInvokeSucc = ((returnNotNull && result != null) || !returnNotNull);
            if (isInvokeSucc) {
                response.setResult(result);
                response.setError("");
                response.setReturnNotNull(returnNotNull);
                injectSuccInvoke(invokeTimespan);
            } else {
                System.err.println(RpcSystemConfig.FILTER_RESPONSE_MSG);
                response.setResult(null);
                response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
                injectFilterInvoke();
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(getStackTrace(t));
            t.printStackTrace();
            System.err.printf("RPC Server invoke error!\n");
            injectFailInvoke(t);
            return Boolean.FALSE;
        } finally {
            release();
        }
    }

    private Object invoke(MethodInvoker mi, MessageRequest request) throws Throwable {
        if (modular != null) {
            ModuleProvider provider = modular.invoke(new ModuleInvoker() {

                @Override
                public Class getInterface() {
                    return mi.getClass().getInterfaces()[0];
                }

                @Override
                public Object invoke(MessageRequest request) throws Throwable {
                    return mi.invoke(request);
                }

                @Override
                public void destroy() {

                }
            }, request);
            return provider.getInvoker().invoke(request);
        } else {
            return mi.invoke(request);
        }
    }

    private Object reflect(MessageRequest request) throws Throwable {
        ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(METHOD_MAPPED_NAME);
        advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
        weaver.addAdvisor(advisor);
        MethodInvoker mi = (MethodInvoker) weaver.getProxy();
        Object obj = invoke(mi, request);
        invokeTimespan = mi.getInvokeTimespan();
        setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice()).isReturnNotNull());
        return obj;
    }

    public String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));

        return buf.toString();
    }

    public boolean isReturnNotNull() {
        return returnNotNull;
    }

    public void setReturnNotNull(boolean returnNotNull) {
        this.returnNotNull = returnNotNull;
    }

    public MessageResponse getResponse() {
        return response;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }

    protected abstract void injectInvoke();

    protected abstract void injectSuccInvoke(long invokeTimespan);

    protected abstract void injectFailInvoke(Throwable error);

    protected abstract void injectFilterInvoke();

    protected abstract void acquire();

    protected abstract void release();
}

