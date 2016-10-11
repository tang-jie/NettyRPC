/**
 * @filename:MessageSendExecutor.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc客户端执行模块
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import com.google.common.reflect.Reflection;
import newlandframework.netty.rpc.serialize.support.RpcSerializeProtocol;

public class MessageSendExecutor {

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    public MessageSendExecutor() {
    }

    public MessageSendExecutor(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void setRpcServerLoader(String serverAddress, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, serializeProtocol);
    }

    public void stop() {
        loader.unLoad();
    }

    public static <T> T execute(Class<T> rpcInterface) {
        return (T) Reflection.newProxy(rpcInterface, new MessageSendProxy<T>());
    }
}
