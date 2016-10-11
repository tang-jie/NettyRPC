/**
 * @filename:KryoCodecUtil.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Kryo编解码工具类
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.kryo;

import com.esotericsoftware.kryo.pool.KryoPool;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import newlandframework.netty.rpc.serialize.support.MessageCodecUtil;
import com.google.common.io.Closer;

public class KryoCodecUtil implements MessageCodecUtil {

    private KryoPool pool;
    private static Closer closer = Closer.create();

    public KryoCodecUtil(KryoPool pool) {
        this.pool = pool;
    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            closer.close();
        }
    }

    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            Object obj = kryoSerialization.deserialize(byteArrayInputStream);
            return obj;
        } finally {
            closer.close();
        }
    }
}
