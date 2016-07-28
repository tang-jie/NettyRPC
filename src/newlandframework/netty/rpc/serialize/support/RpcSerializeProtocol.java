/**
 * @filename:RpcSerializeProtocol.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息序序列化协议类型
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public enum RpcSerializeProtocol {

    JDKSERIALIZE("jdknative"), KRYOSERIALIZE("kryo"), HESSIANSERIALIZE("hessian");

    private String serializeProtocol;

    private RpcSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    public String toString() {
        ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
        return ReflectionToStringBuilder.toString(this);
    }

    public String getProtocol() {
        return serializeProtocol;
    }
}
