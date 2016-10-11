/**
 * @filename:MessageCallBack.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc消息回调
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import newlandframework.netty.rpc.model.MessageRequest;
import newlandframework.netty.rpc.model.MessageResponse;

public class MessageCallBack {

    private MessageRequest request;
    private MessageResponse response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public MessageCallBack(MessageRequest request) {
        this.request = request;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();
            finish.await(10*1000, TimeUnit.MILLISECONDS);
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(MessageResponse reponse) {
        try {
            lock.lock();
            finish.signal();
            this.response = reponse;
        } finally {
            lock.unlock();
        }
    }
}
