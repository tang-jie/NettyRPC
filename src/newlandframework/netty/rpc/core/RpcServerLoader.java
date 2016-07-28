/**
 * @filename:RpcServerLoader.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:rpc服务器配置加载
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import newlandframework.netty.rpc.serialize.support.RpcSerializeProtocol;

public class RpcServerLoader {

    private volatile static RpcServerLoader rpcServerLoader;
    private final static String DELIMITER = ":";
    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDKSERIALIZE;

    private final static int parallel = Runtime.getRuntime().availableProcessors() * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1));
    private MessageSendHandler messageSendHandler = null;

    private Lock lock = new ReentrantLock();
    private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();

    private RpcServerLoader() {
    }

    public static RpcServerLoader getInstance() {
        if (rpcServerLoader == null) {
            synchronized (RpcServerLoader.class) {
                if (rpcServerLoader == null) {
                    rpcServerLoader = new RpcServerLoader();
                }
            }
        }
        return rpcServerLoader;
    }

    public void load(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
        if (ipAddr.length == 2) {
            String host = ipAddr[0];
            int port = Integer.parseInt(ipAddr[1]);
            final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);

            ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(new MessageSendInitializeTask(eventLoopGroup, remoteAddr, serializeProtocol));

            Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
                public void onSuccess(Boolean result) {
                    try {
                        lock.lock();

                        if (messageSendHandler == null) {
                            handlerStatus.await();
                        }

                        if (result == Boolean.TRUE && messageSendHandler != null) {
                            connectStatus.signalAll();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RpcServerLoader.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        lock.unlock();
                    }
                }

                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, threadPoolExecutor);
        }
    }

    public void setMessageSendHandler(MessageSendHandler messageInHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageInHandler;
            handlerStatus.signal();
        } finally {
            lock.unlock();
        }
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                connectStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void unLoad() {
        messageSendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }
}
