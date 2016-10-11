/**
 * @filename:MessageSendProxy.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc客户端消息处理
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import java.lang.reflect.Method;
import java.util.UUID;
import newlandframework.netty.rpc.model.MessageRequest;
import com.google.common.reflect.AbstractInvocationHandler;

public class MessageSendProxy<T> extends AbstractInvocationHandler {

    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParameters(args);

        MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
    }
}
