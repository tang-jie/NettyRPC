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

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MetricsAggregationTask.java
 * @description:MetricsAggregationTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/26
 */
public class MetricsAggregationTask implements Runnable {
    private boolean flag = false;
    private MetricsTask[] tasks;
    private List<ModuleMetricsVisitor> visitors;
    private CountDownLatch latch;

    public MetricsAggregationTask(boolean flag, MetricsTask[] tasks, List<ModuleMetricsVisitor> visitors, CountDownLatch latch) {
        this.flag = flag;
        this.tasks = tasks;
        this.visitors = visitors;
        this.latch = latch;
    }

    @Override
    public void run() {
        if (flag) {
            try {
                for (MetricsTask task : tasks) {
                    //System.out.println(task.getResult().get(0));
                    visitors.add(task.getResult().get(0));
                }
            } finally {
                latch.countDown();
            }
        } else {
            flag = true;
        }
    }
}

