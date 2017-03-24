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
package com.newlandframework.rpc.netty.handler;

import com.newlandframework.rpc.netty.MessageRecvHandler;
import com.newlandframework.rpc.serialize.kryo.KryoCodecUtil;
import com.newlandframework.rpc.serialize.kryo.KryoDecoder;
import com.newlandframework.rpc.serialize.kryo.KryoEncoder;
import com.newlandframework.rpc.serialize.kryo.KryoPoolFactory;
import io.netty.channel.ChannelPipeline;

import java.util.Map;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:KryoRecvHandler.java
 * @description:KryoRecvHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class KryoRecvHandler implements NettyRpcRecvHandler {
    public void handle(Map<String, Object> handlerMap, ChannelPipeline pipeline) {
        KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
        pipeline.addLast(new KryoEncoder(util));
        pipeline.addLast(new KryoDecoder(util));
        pipeline.addLast(new MessageRecvHandler(handlerMap));
    }
}

