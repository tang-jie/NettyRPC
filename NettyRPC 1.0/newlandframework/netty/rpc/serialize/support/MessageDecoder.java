/**
 * @filename:MessageDecoder.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息解码接口
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageDecoder extends ByteToMessageDecoder {

    final public static int MESSAGE_LENGTH = MessageCodecUtil.MESSAGE_LENGTH;
    private MessageCodecUtil util = null;

    public MessageDecoder(final MessageCodecUtil util) {
        this.util = util;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < MessageDecoder.MESSAGE_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();
        
        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object obj = util.decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
                Logger.getLogger(MessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
