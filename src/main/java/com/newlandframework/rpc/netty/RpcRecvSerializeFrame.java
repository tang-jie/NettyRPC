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

import com.newlandframework.rpc.netty.handler.NettyRpcRecvHandler;
import com.newlandframework.rpc.netty.handler.JdkNativeRecvHandler;
import com.newlandframework.rpc.netty.handler.KryoRecvHandler;
import com.newlandframework.rpc.netty.handler.HessianRecvHandler;
import com.newlandframework.rpc.netty.handler.ProtostuffRecvHandler;
import com.newlandframework.rpc.serialize.RpcSerializeFrame;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;
import io.netty.channel.ChannelPipeline;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:RpcRecvSerializeFrame.java
 * @description:RpcRecvSerializeFrame功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class RpcRecvSerializeFrame implements RpcSerializeFrame {

    private Map<String, Object> handlerMap = null;

    public RpcRecvSerializeFrame(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private static ClassToInstanceMap<NettyRpcRecvHandler> handler = MutableClassToInstanceMap.create();

    static {
        handler.putInstance(JdkNativeRecvHandler.class, new JdkNativeRecvHandler());
        handler.putInstance(KryoRecvHandler.class, new KryoRecvHandler());
        handler.putInstance(HessianRecvHandler.class, new HessianRecvHandler());
        handler.putInstance(ProtostuffRecvHandler.class, new ProtostuffRecvHandler());
    }

    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDKSERIALIZE: {
                handler.getInstance(JdkNativeRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case KRYOSERIALIZE: {
                handler.getInstance(KryoRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case HESSIANSERIALIZE: {
                handler.getInstance(HessianRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
            case PROTOSTUFFSERIALIZE: {
                handler.getInstance(ProtostuffRecvHandler.class).handle(handlerMap, pipeline);
                break;
            }
        }
    }
}
