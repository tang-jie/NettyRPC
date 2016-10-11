/**
 * @filename:MessageEncoder.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息编码接口
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Object> {

    private MessageCodecUtil util = null;

    public MessageEncoder(final MessageCodecUtil util) {
        this.util = util;
    }

    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}

