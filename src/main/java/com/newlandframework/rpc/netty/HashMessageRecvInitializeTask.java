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

import com.newlandframework.rpc.core.ReflectionUtils;
import com.newlandframework.rpc.filter.ServiceFilterBinder;
import com.newlandframework.rpc.jmx.HashModuleMetricsVisitor;
import com.newlandframework.rpc.jmx.ModuleMetricsVisitor;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.parallel.HashCriticalSection;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import javax.management.JMException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:HashMessageRecvInitializeTask.java
 * @description:HashMessageRecvInitializeTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/27
 */
public class HashMessageRecvInitializeTask extends AbstractMessageRecvInitializeTask {
    private int hashKey = 0;
    private static HashCriticalSection criticalSection = new HashCriticalSection();
    private AtomicReference<ModuleMetricsVisitor> visitor = new AtomicReference<ModuleMetricsVisitor>();

    public HashMessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        super(request, response, handlerMap);
        hashKey = HashCriticalSection.hash(request.getMessageId());
    }

    @Override
    protected void injectInvoke() {
        Class cls = handlerMap.get(request.getClassName()).getClass();
        boolean binder = ServiceFilterBinder.class.isAssignableFrom(cls);
        if (binder) {
            cls = ((ServiceFilterBinder) handlerMap.get(request.getClassName())).getObject().getClass();
        }

        ReflectionUtils utils = new ReflectionUtils();

        try {
            Method method = ReflectionUtils.getDeclaredMethod(cls, request.getMethodName(), request.getTypeParameters());
            utils.listMethod(method, false);
            String signatureMethod = utils.getProvider().toString().trim();
            int index = getHashVisitorListIndex(signatureMethod);
            List<ModuleMetricsVisitor> metricsVisitor = HashModuleMetricsVisitor.getInstance().getHashVisitorList().get(index);
            visitor.set(metricsVisitor.get(hashKey));
            incrementInvoke(visitor.get());
        } finally {
            utils.clearProvider();
        }
    }

    @Override
    protected void injectSuccInvoke(long invokeTimespan) {
        incrementInvokeSucc(visitor.get(), invokeTimespan);
    }

    @Override
    protected void injectFailInvoke(Throwable error) {
        incrementInvokFail(visitor.get(), error);
    }

    @Override
    protected void injectFilterInvoke() {
        incrementInvokFilter(visitor.get());
    }

    @Override
    protected void acquire() {
        criticalSection.enter(hashKey);
    }

    @Override
    protected void release() {
        criticalSection.exit(hashKey);
    }

    private int getHashVisitorListIndex(String signatureMethod) {
        int index = 0;
        int size = HashModuleMetricsVisitor.getInstance().getHashModuleMetricsVisitorListSize();
        breakFor:
        for (index = 0; index < size; index++) {
            Iterator iterator = new FilterIterator(HashModuleMetricsVisitor.getInstance().getHashVisitorList().get(index).iterator(), new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    String statModuleName = ((ModuleMetricsVisitor) object).getModuleName();
                    String statMethodName = ((ModuleMetricsVisitor) object).getMethodName();
                    return statModuleName.compareTo(request.getClassName()) == 0 && statMethodName.compareTo(signatureMethod) == 0;
                }
            });

            while (iterator.hasNext()) {
                break breakFor;
            }
        }
        return index;
    }

    private void incrementInvoke(ModuleMetricsVisitor visitor) {
        visitor.setHashKey(hashKey);
        visitor.incrementInvokeCount();
    }

    private void incrementInvokeSucc(ModuleMetricsVisitor visitor, long invokeTimespan) {
        visitor.incrementInvokeSuccCount();
        visitor.getHistogram().record(invokeTimespan);
        visitor.setInvokeTimespan(invokeTimespan);

        if (invokeTimespan < visitor.getInvokeMinTimespan()) {
            visitor.setInvokeMinTimespan(invokeTimespan);
        }
        if (invokeTimespan > visitor.getInvokeMaxTimespan()) {
            visitor.setInvokeMaxTimespan(invokeTimespan);
        }
    }

    private void incrementInvokFail(ModuleMetricsVisitor visitor, Throwable error) {
        visitor.incrementInvokeFailCount();
        visitor.setLastStackTrace((Exception) error);
        try {
            visitor.buildErrorCompositeData(error);
        } catch (JMException e) {
            e.printStackTrace();
        }
    }

    private void incrementInvokFilter(ModuleMetricsVisitor visitor) {
        visitor.incrementInvokeFilterCount();
    }
}

