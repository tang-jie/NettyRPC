/**
 * @filename:RpcServerJdkNativeProtocolStarter.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:rpc服务器启动模块
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcServerJdkNativeProtocolStarter {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("newlandframework/netty/rpc/config/rpc-invoke-config-jdknative.xml");
    }
}

