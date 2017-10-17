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
package com.newlandframework.rpc.jmx;

import com.alibaba.druid.util.Histogram;

import javax.management.JMException;
import javax.management.openmbean.*;
import java.beans.ConstructorProperties;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:ModuleMetricsVisitor.java
 * @description:ModuleMetricsVisitor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public class ModuleMetricsVisitor {
    private String moduleName;
    private String methodName;
    private volatile long invokeCount = 0L;
    private volatile long invokeSuccCount = 0L;
    private volatile long invokeFailCount = 0L;
    private volatile long invokeFilterCount = 0L;
    private long invokeTimespan = 0L;
    private long invokeMinTimespan = 3600 * 1000L;
    private long invokeMaxTimespan = 0L;
    private long invokeHistogram[];
    private Exception lastStackTrace;
    private String lastStackTraceDetail;
    private long lastErrorTime;

    private Histogram histogram = new Histogram(TimeUnit.MILLISECONDS, new long[]{1, 10, 100, 1000, 10 * 1000, 100 * 1000, 1000 * 1000});

    private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeCount");
    private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeSuccCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeSuccCount");
    private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFailCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeFailCount");
    private final AtomicLongFieldUpdater<ModuleMetricsVisitor> invokeFilterCountUpdater = AtomicLongFieldUpdater.newUpdater(ModuleMetricsVisitor.class, "invokeFilterCount");

    private static final String[] THROWABLE_NAMES = {"message", "class", "stackTrace"};
    private static final String[] THROWABLE_DESCRIPTIONS = {"message", "class", "stackTrace"};
    private static final OpenType<?>[] THROWABLE_TYPES = new OpenType<?>[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};
    private static CompositeType THROWABLE_COMPOSITE_TYPE = null;

    @ConstructorProperties({"moduleName", "methodName"})
    public ModuleMetricsVisitor(String moduleName, String methodName) {
        this.moduleName = moduleName;
        this.methodName = methodName;
    }

    public void reset() {
        moduleName = "";
        methodName = "";
        lastStackTraceDetail = "";
        invokeTimespan = 0L;
        invokeMinTimespan = 0L;
        invokeMaxTimespan = 0L;
        lastErrorTime = 0L;
        lastStackTrace = null;
        invokeCountUpdater.set(this, 0);
        invokeSuccCountUpdater.set(this, 0);
        invokeFailCountUpdater.set(this, 0);
        invokeFilterCountUpdater.set(this, 0);
        histogram.reset();
    }

    public String getErrorLastTime() {
        if (lastErrorTime <= 0) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(lastErrorTime));
    }

    public String getLastStackTrace() {
        if (lastStackTrace == null) {
            return null;
        }

        StringWriter buf = new StringWriter();
        lastStackTrace.printStackTrace(new PrintWriter(buf));
        return buf.toString();
    }

    public String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));

        return buf.toString();
    }

    public void setLastStackTrace(Exception lastStackTrace) {
        this.lastStackTrace = lastStackTrace;
        this.lastStackTraceDetail = getLastStackTrace();
        this.lastErrorTime = System.currentTimeMillis();
    }

    public String getLastStackTraceDetail() {
        return lastStackTraceDetail;
    }

    public CompositeType getThrowableCompositeType() throws JMException {
        if (THROWABLE_COMPOSITE_TYPE == null) {
            THROWABLE_COMPOSITE_TYPE = new CompositeType("Throwable",
                    "Throwable",
                    THROWABLE_NAMES,
                    THROWABLE_DESCRIPTIONS,
                    THROWABLE_TYPES);
        }

        return THROWABLE_COMPOSITE_TYPE;
    }

    public CompositeData buildErrorCompositeData(Throwable error) throws JMException {
        if (error == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>(512);

        map.put("class", error.getClass().getName());
        map.put("message", error.getMessage());
        map.put("stackTrace", getStackTrace(error));

        return new CompositeDataSupport(getThrowableCompositeType(), map);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getInvokeCount() {
        return this.invokeCountUpdater.get(this);
    }

    public void setInvokeCount(long invokeCount) {
        this.invokeCountUpdater.set(this, invokeCount);
    }

    public long incrementInvokeCount() {
        return this.invokeCountUpdater.incrementAndGet(this);
    }

    public long getInvokeSuccCount() {
        return this.invokeSuccCountUpdater.get(this);
    }

    public void setInvokeSuccCount(long invokeSuccCount) {
        this.invokeSuccCountUpdater.set(this, invokeSuccCount);
    }

    public long incrementInvokeSuccCount() {
        return this.invokeSuccCountUpdater.incrementAndGet(this);
    }

    public long getInvokeFailCount() {
        return this.invokeFailCountUpdater.get(this);
    }

    public void setInvokeFailCount(long invokeFailCount) {
        this.invokeFailCountUpdater.set(this, invokeFailCount);
    }

    public long incrementInvokeFailCount() {
        return this.invokeFailCountUpdater.incrementAndGet(this);
    }

    public long getInvokeFilterCount() {
        return this.invokeFilterCountUpdater.get(this);
    }

    public void setInvokeFilterCount(long invokeFilterCount) {
        this.invokeFilterCountUpdater.set(this, invokeFilterCount);
    }

    public long incrementInvokeFilterCount() {
        return this.invokeFilterCountUpdater.incrementAndGet(this);
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public long[] getInvokeHistogram() {
        return histogram.toArray();
    }

    public long getInvokeTimespan() {
        return invokeTimespan;
    }

    public void setInvokeTimespan(long invokeTimespan) {
        this.invokeTimespan = invokeTimespan;
    }

    public long getInvokeMinTimespan() {
        return invokeMinTimespan;
    }

    public void setInvokeMinTimespan(long invokeMinTimespan) {
        this.invokeMinTimespan = invokeMinTimespan;
    }

    public long getInvokeMaxTimespan() {
        return invokeMaxTimespan;
    }

    public void setInvokeMaxTimespan(long invokeMaxTimespan) {
        this.invokeMaxTimespan = invokeMaxTimespan;
    }
}

