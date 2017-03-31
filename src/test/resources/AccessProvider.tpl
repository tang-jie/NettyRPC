package com.newlandframework.test;

import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: 2017/3/30 by tangjie
// java source only support public method
public class RpcServerAccessProvider {
    public String getRpcServerTime(String message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "NettyRpc server receive:" + message + " , server time is:" + df.format(new Date());
    }

    public void sayHello() {
        System.out.println("Hello NettyRpc!");
    }
}

