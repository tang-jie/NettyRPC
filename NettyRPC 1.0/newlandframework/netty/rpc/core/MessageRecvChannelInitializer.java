/**
 * @filename:MessageRecvChannelInitializer.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc服务端管道初始化
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import java.util.Map;
import newlandframework.netty.rpc.serialize.support.RpcSerializeProtocol;

public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcSerializeProtocol protocol;
    private RpcRecvSerializeFrame frame = null;

    MessageRecvChannelInitializer buildRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
        frame = new RpcRecvSerializeFrame(handlerMap);
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        frame.select(protocol, pipeline);
    }
}
