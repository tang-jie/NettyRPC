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


import org.apache.commons.collections.iterators.UniqueFilterIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MetricsTask.java
 * @description:MetricsTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/26
 */
public class MetricsTask implements Runnable {
    private final CyclicBarrier barrier;
    private List<ModuleMetricsVisitor> visitorList;
    private List<ModuleMetricsVisitor> result = new ArrayList<ModuleMetricsVisitor>();

    public MetricsTask(CyclicBarrier barrier, List<ModuleMetricsVisitor> visitorList) {
        this.barrier = barrier;
        this.visitorList = visitorList;
    }

    @Override
    public void run() {
        try {
            barrier.await();
            accumulate();
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void count(List<ModuleMetricsVisitor> list) {
        for (int i = 0; i < result.size(); i++) {
            long invokeCount = 0L;
            long invokeSuccCount = 0L;
            long invokeFailCount = 0L;
            long invokeFilterCount = 0L;
            long invokeTimespan = 0L;
            long invokeMinTimespan = list.get(0).getInvokeMinTimespan();
            long invokeMaxTimespan = list.get(0).getInvokeMaxTimespan();
            int length = result.get(i).getHistogram().getRanges().length + 1;
            long[] invokeHistogram = new long[length];
            Arrays.fill(invokeHistogram, 0L);
            String lastStackTraceDetail = "";
            long lastErrorTime = list.get(0).getErrorLastTimeLongVal();

            ModuleMetrics metrics = new ModuleMetrics();
            metrics.setInvokeCount(invokeCount);
            metrics.setInvokeSuccCount(invokeSuccCount);
            metrics.setInvokeFailCount(invokeFailCount);
            metrics.setInvokeFilterCount(invokeFilterCount);
            metrics.setInvokeTimespan(invokeTimespan);
            metrics.setInvokeMinTimespan(invokeMinTimespan);
            metrics.setInvokeMaxTimespan(invokeMaxTimespan);
            metrics.setInvokeHistogram(invokeHistogram);
            metrics.setLastStackTraceDetail(lastStackTraceDetail);
            metrics.setLastErrorTime(lastErrorTime);

            merge(i, list, metrics);

            result.get(i).setInvokeCount(metrics.getInvokeCount());
            result.get(i).setInvokeSuccCount(metrics.getInvokeSuccCount());
            result.get(i).setInvokeFailCount(metrics.getInvokeFailCount());
            result.get(i).setInvokeFilterCount(metrics.getInvokeFilterCount());
            result.get(i).setInvokeTimespan(metrics.getInvokeTimespan());
            result.get(i).setInvokeMaxTimespan(metrics.getInvokeMaxTimespan());
            result.get(i).setInvokeMinTimespan(metrics.getInvokeMinTimespan());
            result.get(i).setInvokeHistogram(metrics.getInvokeHistogram());

            if (metrics.getLastErrorTime() > 0) {
                result.get(i).setErrorLastTimeLongVal(metrics.getLastErrorTime());
                result.get(i).setLastStackTraceDetail(metrics.getLastStackTraceDetail());
            }
        }
    }

    private void merge(int index, List<ModuleMetricsVisitor> list, ModuleMetrics metrics) {
        long invokeCount = metrics.getInvokeCount();
        long invokeSuccCount = metrics.getInvokeSuccCount();
        long invokeFailCount = metrics.getInvokeFailCount();
        long invokeFilterCount = metrics.getInvokeFilterCount();
        long invokeTimespan = metrics.getInvokeTimespan();
        long invokeMinTimespan = metrics.getInvokeMinTimespan();
        long invokeMaxTimespan = metrics.getInvokeMaxTimespan();
        long[] invokeHistogram = metrics.getInvokeHistogram();
        String lastStackTraceDetail = metrics.getLastStackTraceDetail();
        long lastErrorTime = metrics.getLastErrorTime();

        for (int i = 0; i < list.size(); i++) {
            boolean find = equals(result.get(index).getModuleName(), list.get(i).getModuleName(), result.get(index).getMethodName(), list.get(i).getMethodName());
            if (find) {
                invokeCount += list.get(i).getInvokeCount();
                invokeSuccCount += list.get(i).getInvokeSuccCount();
                invokeFailCount += list.get(i).getInvokeFailCount();
                invokeFilterCount += list.get(i).getInvokeFilterCount();
                long timespan = list.get(i).getInvokeTimespan();
                if (timespan > 0) {
                    invokeTimespan = timespan;
                }
                long minTimespan = list.get(i).getInvokeMinTimespan();
                long maxTimespan = list.get(i).getInvokeMaxTimespan();
                if (minTimespan < invokeMinTimespan) {
                    invokeMinTimespan = minTimespan;
                }
                if (maxTimespan > invokeMaxTimespan) {
                    invokeMaxTimespan = maxTimespan;
                }

                for (int j = 0; j < invokeHistogram.length; j++) {
                    invokeHistogram[j] += list.get(i).getHistogram().toArray()[j];
                }

                long fail = list.get(i).getInvokeFailCount();
                if (fail > 0) {
                    long lastTime = list.get(i).getErrorLastTimeLongVal();
                    if (lastTime > lastErrorTime) {
                        lastErrorTime = lastTime;
                        lastStackTraceDetail = list.get(i).getLastStackTraceDetail();
                    }
                }
            }
        }

        metrics.setInvokeCount(invokeCount);
        metrics.setInvokeSuccCount(invokeSuccCount);
        metrics.setInvokeFailCount(invokeFailCount);
        metrics.setInvokeFilterCount(invokeFilterCount);
        metrics.setInvokeTimespan(invokeTimespan);
        metrics.setInvokeMinTimespan(invokeMinTimespan);
        metrics.setInvokeMaxTimespan(invokeMaxTimespan);
        metrics.setInvokeHistogram(invokeHistogram);
        metrics.setLastStackTraceDetail(lastStackTraceDetail);
        metrics.setLastErrorTime(lastErrorTime);
    }

    private void accumulate() {
        List<ModuleMetricsVisitor> list = visitorList;

        Iterator iterator = new UniqueFilterIterator(list.iterator());
        while (iterator.hasNext()) {
            ModuleMetricsVisitor visitor = (ModuleMetricsVisitor) iterator.next();
            result.add(new ModuleMetricsVisitor(visitor.getModuleName(), visitor.getMethodName()));
        }

        count(list);
    }

    private boolean equals(String srcModuleName, String destModuleName, String srcMethodName, String destMethodName) {
        return srcModuleName.equals(destModuleName) && srcMethodName.equals(destMethodName);
    }

    public List<ModuleMetricsVisitor> getResult() {
        return result;
    }

    public void setResult(List<ModuleMetricsVisitor> result) {
        this.result = result;
    }

    private class ModuleMetrics {
        private long invokeCount;
        private long invokeSuccCount;
        private long invokeFailCount;
        private long invokeFilterCount;
        private long invokeTimespan;
        private long invokeMinTimespan;
        private long invokeMaxTimespan;
        private long[] invokeHistogram;
        private String lastStackTraceDetail;
        private long lastErrorTime;

        public long getInvokeCount() {
            return invokeCount;
        }

        public void setInvokeCount(long invokeCount) {
            this.invokeCount = invokeCount;
        }

        public long getInvokeSuccCount() {
            return invokeSuccCount;
        }

        public void setInvokeSuccCount(long invokeSuccCount) {
            this.invokeSuccCount = invokeSuccCount;
        }

        public long getInvokeFailCount() {
            return invokeFailCount;
        }

        public void setInvokeFailCount(long invokeFailCount) {
            this.invokeFailCount = invokeFailCount;
        }

        public long getInvokeFilterCount() {
            return invokeFilterCount;
        }

        public void setInvokeFilterCount(long invokeFilterCount) {
            this.invokeFilterCount = invokeFilterCount;
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

        public long[] getInvokeHistogram() {
            return invokeHistogram;
        }

        public void setInvokeHistogram(long[] invokeHistogram) {
            this.invokeHistogram = invokeHistogram;
        }

        public String getLastStackTraceDetail() {
            return lastStackTraceDetail;
        }

        public void setLastStackTraceDetail(String lastStackTraceDetail) {
            this.lastStackTraceDetail = lastStackTraceDetail;
        }

        public long getLastErrorTime() {
            return lastErrorTime;
        }

        public void setLastErrorTime(long lastErrorTime) {
            this.lastErrorTime = lastErrorTime;
        }
    }
}

