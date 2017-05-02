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
package com.newlandframework.rpc.netty;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorCompletionService;
import java.util.logging.Level;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.parallel.NamedThreadFactory;
import com.newlandframework.rpc.parallel.RpcThreadPool;
import com.newlandframework.rpc.model.MessageKeyVal;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;
import com.newlandframework.rpc.compiler.AccessAdaptiveProvider;
import com.newlandframework.rpc.core.AbilityDetailProvider;
import com.newlandframework.rpc.netty.resolver.ApiEchoResolver;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MessageRecvExecutor.java
 * @description:MessageRecvExecutor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MessageRecvExecutor implements ApplicationContextAware {

    private String serverAddress;
    private int echoApiPort;
    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDKSERIALIZE;
    private static final String DELIMITER = RpcSystemConfig.DELIMITER;
    private int parallel = RpcSystemConfig.PARALLEL * 2;
    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
    private static volatile ListeningExecutorService threadPoolExecutor;
    private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();
    private int numberOfEchoThreadsPool = 1;

    ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup(parallel, threadRpcFactory, SelectorProvider.provider());

    public MessageRecvExecutor() {
        handlerMap.clear();
        register();
    }

    private static class MessageRecvExecutorHolder {
        static final MessageRecvExecutor instance = new MessageRecvExecutor();
    }

    public static MessageRecvExecutor getInstance() {
        return MessageRecvExecutorHolder.instance;
    }

    public static void submit(Callable<Boolean> task, final ChannelHandlerContext ctx, final MessageRequest request, final MessageResponse response) {
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) (RpcSystemConfig.isMonitorServerSupport() ? RpcThreadPool.getExecutorWithJmx(threadNums, queueNums) : RpcThreadPool.getExecutor(threadNums, queueNums)));
                }
            }
        }

        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean result) {
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("RPC Server Send message-id respone:" + request.getMessageId());
                    }
                });
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        try {
            MessageKeyVal keyVal = (MessageKeyVal) ctx.getBean(Class.forName("com.newlandframework.rpc.model.MessageKeyVal"));
            Map<String, Object> rpcServiceObject = keyVal.getMessageKeyVal();

            Set s = rpcServiceObject.entrySet();
            Iterator<Map.Entry<String, Object>> it = s.iterator();
            Map.Entry<String, Object> entry;

            while (it.hasNext()) {
                entry = it.next();
                handlerMap.put(entry.getKey(), entry.getValue());
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MessageRecvExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] ipAddr = serverAddress.split(MessageRecvExecutor.DELIMITER);

            if (ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = null;
                future = bootstrap.bind(host, port).sync();

                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            ExecutorService executor = Executors.newFixedThreadPool(numberOfEchoThreadsPool);
                            ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executor);
                            completionService.submit(new ApiEchoResolver(host, echoApiPort));
                            System.out.printf("[author tangjie] Netty RPC Server start success!\nip:%s\nport:%d\nprotocol:%s\n\n", host, port, serializeProtocol);
                            channelFuture.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    executor.shutdownNow();
                                }
                            });
                        }
                    }
                });
            } else {
                System.out.printf("[author tangjie] Netty RPC Server start fail!\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    private void register() {
        handlerMap.put(RpcSystemConfig.RPC_COMPILER_SPI_ATTR, new AccessAdaptiveProvider());
        handlerMap.put(RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_ATTR, new AbilityDetailProvider());
    }

    public Map<String, Object> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcSerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    public int getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(int echoApiPort) {
        this.echoApiPort = echoApiPort;
    }
}
