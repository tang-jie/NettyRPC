/**
 * @filename:MessageSendChannelInitializer.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc客户端管道初始化
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import newlandframework.netty.rpc.serialize.support.RpcSerializeProtocol;

public class MessageSendChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcSerializeProtocol protocol;
    private RpcSendSerializeFrame frame = new RpcSendSerializeFrame();

    MessageSendChannelInitializer buildRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }
    
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        frame.select(protocol, pipeline);
    }
}
