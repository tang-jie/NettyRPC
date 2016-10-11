/**
 * @filename:KryoDecoder.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Kryo解码器
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.kryo;

import newlandframework.netty.rpc.serialize.support.MessageCodecUtil;
import newlandframework.netty.rpc.serialize.support.MessageDecoder;

public class KryoDecoder extends MessageDecoder {

    public KryoDecoder(MessageCodecUtil util) {
        super(util);
    }
}

