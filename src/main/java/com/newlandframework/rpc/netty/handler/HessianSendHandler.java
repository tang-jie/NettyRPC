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

import com.newlandframework.rpc.netty.MessageSendHandler;
import com.newlandframework.rpc.serialize.hessian.HessianCodecUtil;
import com.newlandframework.rpc.serialize.hessian.HessianDecoder;
import com.newlandframework.rpc.serialize.hessian.HessianEncoder;
import io.netty.channel.ChannelPipeline;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:HessianSendHandler.java
 * @description:HessianSendHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class HessianSendHandler implements NettyRpcSendHandler {
    public void handle(ChannelPipeline pipeline) {
        HessianCodecUtil util = new HessianCodecUtil();
        pipeline.addLast(new HessianEncoder(util));
        pipeline.addLast(new HessianDecoder(util));
        pipeline.addLast(new MessageSendHandler());
    }
}

