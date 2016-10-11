/**
 * @filename:RpcSendSerializeFrame.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC客户端消息序列化协议框架
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import newlandframework.netty.rpc.serialize.support.MessageCodecUtil;
import newlandframework.netty.rpc.serialize.support.hessian.HessianCodecUtil;
import newlandframework.netty.rpc.serialize.support.hessian.HessianDecoder;
import newlandframework.netty.rpc.serialize.support.hessian.HessianEncoder;
import newlandframework.netty.rpc.serialize.support.kryo.KryoCodecUtil;
import newlandframework.netty.rpc.serialize.support.kryo.KryoDecoder;
import newlandframework.netty.rpc.serialize.support.kryo.KryoEncoder;
import newlandframework.netty.rpc.serialize.support.kryo.KryoPoolFactory;
import newlandframework.netty.rpc.serialize.support.RpcSerializeFrame;
import newlandframework.netty.rpc.serialize.support.RpcSerializeProtocol;

public class RpcSendSerializeFrame implements RpcSerializeFrame {

    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDKSERIALIZE: {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MessageCodecUtil.MESSAGE_LENGTH, 0, MessageCodecUtil.MESSAGE_LENGTH));
                pipeline.addLast(new LengthFieldPrepender(MessageCodecUtil.MESSAGE_LENGTH));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                pipeline.addLast(new MessageSendHandler());
                break;
            }
            case KRYOSERIALIZE: {
                KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
                pipeline.addLast(new KryoEncoder(util));
                pipeline.addLast(new KryoDecoder(util));
                pipeline.addLast(new MessageSendHandler());
                break;
            }
            case HESSIANSERIALIZE: {
                HessianCodecUtil util = new HessianCodecUtil();
                pipeline.addLast(new HessianEncoder(util));
                pipeline.addLast(new HessianDecoder(util));
                pipeline.addLast(new MessageSendHandler());
                break;
            }
        }
    }
}
