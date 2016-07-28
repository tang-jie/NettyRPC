/**
 * @filename:MessageRecvInitializeTask.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Rpc服务器消息线程任务处理
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.Callable;
import newlandframework.netty.rpc.model.MessageRequest;
import newlandframework.netty.rpc.model.MessageResponse;
import org.apache.commons.lang.reflect.MethodUtils;

public class MessageRecvInitializeTask implements Callable<Boolean> {

    private MessageRequest request = null;
    private MessageResponse response = null;
    private Map<String, Object> handlerMap = null;
    private ChannelHandlerContext ctx = null;

    public MessageResponse getResponse() {
        return response;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }

    MessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
        this.ctx = ctx;
    }

    public Boolean call() {
        response.setMessageId(request.getMessageId());
        try {
            Object result = reflect(request);
            response.setResult(result);
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(t.toString());
            t.printStackTrace();
            System.err.printf("RPC Server invoke error!\n");
            return Boolean.FALSE;
        }
    }

    private Object reflect(MessageRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
    }
}
