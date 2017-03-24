/**
 * Copyright (C) 2016 Newland Group Holding Limited
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
package com.newlandframework.test;

import com.newlandframework.rpc.services.MultiCalculate;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MultiCalcParallelRequestThread.java
 * @description:MultiCalcParallelRequestThread功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MultiCalcParallelRequestThread implements Runnable {

    private CountDownLatch signal;
    private CountDownLatch finish;
    private int taskNumber = 0;
    private MultiCalculate calc;

    public MultiCalcParallelRequestThread(MultiCalculate calc, CountDownLatch signal, CountDownLatch finish, int taskNumber) {
        this.signal = signal;
        this.finish = finish;
        this.taskNumber = taskNumber;
        this.calc = calc;
    }

    public void run() {
        try {
            signal.await();

            int multi = calc.multi(taskNumber, taskNumber);
            System.out.println("calc multi result:[" + multi + "]");

            finish.countDown();
        } catch (InterruptedException ex) {
            Logger.getLogger(MultiCalcParallelRequestThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

