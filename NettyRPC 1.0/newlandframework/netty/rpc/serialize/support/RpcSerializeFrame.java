/**
 * @filename:RpcSerializeFrame.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息序序列化协议选择器接口
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import io.netty.channel.ChannelPipeline;

public interface RpcSerializeFrame {

    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline);
}
