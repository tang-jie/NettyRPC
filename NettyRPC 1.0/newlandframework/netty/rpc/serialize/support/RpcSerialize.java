/**
 * @filename:RpcSerialize.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:RPC消息序列化/反序列化接口定义
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RpcSerialize {

    void serialize(OutputStream output, Object object) throws IOException;

    Object deserialize(InputStream input) throws IOException;
}
