/**
 * @filename:MessageCodecUtil.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息编解码接口
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public interface MessageCodecUtil {

    final public static int MESSAGE_LENGTH = 4;

    public void encode(final ByteBuf out, final Object message) throws IOException;

    public Object decode(byte[] body) throws IOException;
}
