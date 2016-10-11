/**
 * @filename:KryoEncoder.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Kryo编码器
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.kryo;

import newlandframework.netty.rpc.serialize.support.MessageCodecUtil;
import newlandframework.netty.rpc.serialize.support.MessageEncoder;

public class KryoEncoder extends MessageEncoder {

    public KryoEncoder(MessageCodecUtil util) {
        super(util);
    }
}
