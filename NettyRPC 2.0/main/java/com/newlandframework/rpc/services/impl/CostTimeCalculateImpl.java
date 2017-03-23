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
package com.newlandframework.rpc.services.impl;

import com.newlandframework.rpc.services.CostTimeCalculate;
import com.newlandframework.rpc.services.pojo.CostTime;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:CostTimeCalculateImpl.java
 * @description:CostTimeCalculateImpl功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class CostTimeCalculateImpl implements CostTimeCalculate {
    public CostTime calculate() {
        CostTime elapse = new CostTime();
        try {
            long start = 0, end = 0;
            start = System.currentTimeMillis();
            //模拟耗时操作
            Thread.sleep(3000L);
            end = System.currentTimeMillis();

            long interval = end - start;
            elapse.setElapse(interval);
            elapse.setDetail("I'm XiaoHaoBaby,cost time operate succ!");
            System.out.println("calculate time:" + interval);
            return elapse;
        } catch (InterruptedException e) {
            e.printStackTrace();
            elapse.setDetail("I'm XiaoHaoBaby,cost time operate fail!");
            return elapse;
        }
    }
}

