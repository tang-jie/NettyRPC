/**
 * @filename:MessageResponse.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:rpc服务应答结构
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class MessageResponse implements Serializable {

    private String messageId;
    private String error;
    private Object resultDesc;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return resultDesc;
    }

    public void setResult(Object resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
