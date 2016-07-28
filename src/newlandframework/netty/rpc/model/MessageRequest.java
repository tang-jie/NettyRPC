/**
 * @filename:MessageRequest.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:rpc服务请求结构
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class MessageRequest implements Serializable {

    private String messageId;
    private String className;
    private String methodName;
    private Class<?>[] typeParameters;
    private Object[] parametersVal;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(Class<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public Object[] getParameters() {
        return parametersVal;
    }

    public void setParameters(Object[] parametersVal) {
        this.parametersVal = parametersVal;
    }

    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[]{"typeParameters", "parametersVal"});
    }
}
