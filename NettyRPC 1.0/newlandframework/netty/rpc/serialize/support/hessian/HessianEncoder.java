/**
 * @filename:HessianEncoder.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Hessian编码器
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.hessian;

import newlandframework.netty.rpc.serialize.support.MessageCodecUtil;
import newlandframework.netty.rpc.serialize.support.MessageEncoder;

public class HessianEncoder extends MessageEncoder {

    public HessianEncoder(MessageCodecUtil util) {
        super(util);
    }
}
