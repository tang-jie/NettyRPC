# NettyRPC Project
high performance java rpc server base on netty framework,using kryo,hessian,protostuff support rpc message serialization.

----------
## NettyRPC开发指南
有兴趣的同学可以参考：[NettyRPC入门手册](https://github.com/tang-jie/NettyRPC/wiki/NettyRPC%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)。

----------
## NettyRPC 1.0 Build 2016/6/25

### NettyRPC 1.0 中文简介：
**NettyRPC是基于Netty构建的RPC系统，消息网络传输支持目前主流的编码解码器**
* NettyRPC基于Java语言进行编写，网络通讯依赖Netty。
* RPC服务端采用线程池对RPC调用进行异步回调处理。
* 服务定义、实现，通过Spring容器进行加载、卸载。
* 消息网络传输除了JDK原生的对象序列化方式，还支持目前主流的编码解码器：kryo、hessian。
* Netty网络模型采用主从Reactor线程模型，提升RPC服务器并行吞吐性能。
* 多线程模型采用guava线程库进行封装。

### NettyRPC 1.0 English Introduction:
**NettyRPC is based on Netty to build the RPC system, the message network transmission support the current mainstream codec.**
* NettyRPC based on Java language, network communications rely on Netty.
* RPC server using the thread pool on the RPC call asynchronous callback processing.
* service definition, implementation, through the Spring container(IOC) loading, unloading.
* message network transmission in addition to JDK native object serialization mode, but also to support the current mainstream of the codec: kryo, hessian.
* Netty network model uses the master-slave Reactor thread model, to improve the performance of RPC server parallel throughput.
* multi thread model using guava thread framework.

----------
## NettyRPC 2.0 Build 2016/10/7

### NettyRPC 2.0 中文简介：
**NettyRPC 2.0是基于NettyRPC 1.0 在Maven下构建的RPC系统，在原有1.0版本的基础上对代码进行重构升级，主要改进点如下：**
* RPC服务启动、注册、卸载支持通过Spring中的nettyrpc标签进行统一管理。
* 在原来编码解码器：JDK原生的对象序列化方式、kryo、hessian，新增了：protostuff。
* 优化了NettyRPC服务端的线程池模型，支持LinkedBlockingQueue、ArrayBlockingQueue、SynchronousQueue，并扩展了多个线程池任务处理策略。
* NettyRPC服务端加入JMX监控支持。

### NettyRPC 2.0 English Introduction:
**NettyRPC 2.0 is based on NettyRPC 1.0 under the Maven to build the RPC system, based on the original 1.0 version of the code to refactoring, the main improvements are as follows:**
* RPC service startup, registration, uninstall support through the nettyrpc Spring tags for unified management.
* in the original codec: JDK native object serialization mode, kryo, hessian, added: protostuff.
* optimize the NettyRPC server's thread pool model, support LinkedBlockingQueue, ArrayBlockingQueue, SynchronousQueue, and expand the various thread pool task processing strategy.
* NettyRPC JMX monitoring support.

----------
## NettyRPC 2.1 Build 2017/3/23

**在NettyRPC 2.0的基础上新增NettyRPC异步回调功能模块：**
* 基于cglib生成异步代理Mock对象，针对一些极端耗时的RPC调用场景进行异步回调，从而提高客户端的并行吞吐量。

----------
## NettyRPC 2.2 Build 2017/5/2

**在2.1版本的基础上，提供NettyRPC服务端接口能力展现功能：**
* 接口能力展现功能模块部署在服务端的18886端口，可以在浏览器中输入：http://ip地址:18886/NettyRPC.html  进行查看。
* 比如在浏览器的地址栏中输入：http://10.1.1.76:18886/NettyRPC.html，  可以查看NettyRPC服务器对外暴露的服务能力接口信息。
* NettyRPC客户端支持重连功能：这点主要是针对RPC服务器宕机的情形下，RPC客户端可以检测链路情况，如果链路不通，则自动重连（重连重试的时间默认为10s）。

----------
## NettyRPC 2.3 Build 2017/7/28

**在NettyRPC 2.2的基础上新增NettyRPC过滤器功能：**
* 进一步合理地分配和利用服务端的系统资源，NettyRPC可以针对某些特定的RPC请求，进行过滤拦截。
* 具体过滤器要实现：com.newlandframework.rpc.filter.Filter接口定义的方法。
* 被拦截到的RPC请求，NettyRPC框架会抛出com.newlandframework.rpc.exception.RejectResponeException异常，可以根据需要进行捕获。
* spring配置文件中的nettyrpc:service标签，新增filter属性，用来定义这个服务对应的过滤器的实现。当然，filter属性是可选的。

----------
## NettyRPC 2.4 Build 2017/8/31

**在NettyRPC 2.3的基础上，增强了RPC服务端动态加载字节码时，对于热点方法的拦截判断能力：**
* 在之前的NettyRPC版本中，RPC服务端集成了一个功能：针对Java HotSpot虚拟机的热加载特性，可以动态加载、生成并执行客户端的热点代码。然而却有一定的风险。因为这些代码中的某些方法，可能存在一些危及服务端安全的操作，所以有必要对这些方法进行拦截控制。
* 技术难点在于：如何对服务端生成的字节码文件进行渲染加工？以往传统的方式，都是基于类进行代理渲染，而这次是针对字节码文件进行织入渲染，最终把拦截方法织入原有的字节码文件中。
* 对字节码操作可选的方案有Byte Code Engineering Library (BCEL)、ASM等。最终从执行性能上考虑，决定采用偏向底层的ASM，对字节码进行渲染织入增强，以节省性能开销。最终通过类加载器，重新把渲染后的字节码，载入运行时上下文环境。
* 具体方法拦截器要实现：com.newlandframework.rpc.compiler.intercept.Interceptor接口定义的方法。NettyRPC框架提供了一个简易的拦截器实现：SimpleMethodInterceptor，可以在这里加入你的拦截判断逻辑。

----------
## NettyRPC 2.5 Build 2017/10/13

**在NettyRPC 2.4的基础上，基于JMX（Java Management Extensions）技术，对NettyRPC的服务端进行调用监控，加强服务端对调用请求的分析统计能力：**
* 是否开启JMX监控，可以通过环境变量：nettyrpc.jmx.invoke.metrics来控制（为0表示关闭JMX监控；非0表示打开JMX监控）。对应NettyRPC系统变量为：RpcSystemConfig.SYSTEM_PROPERTY_JMX_INVOKE_METRICS。如果开启JMX监控，启动的时候，控制台上会打印JMX URL信息。
* JMX监控的URL地址格式为：service:jmx:rmi:///jndi/rmi://服务器ip地址:1098/NettyRPCServer。比如：service:jmx:rmi:///jndi/rmi://10.1.8.5:1098/NettyRPCServer，然后可以在jconsole中，通过JMX对NettyRPC服务端的调用情况进行监控。
* 目前服务端监控的维度主要有：调用次数、调用成功次数、调用失败次数、过滤拦截次数、调用时长、调用最大时长、调用最小时长、错误明细、最后一次错误发生的时间、调用时长统计数组区间。
* 目前暂时只支持jconsole方式，后续会考虑在NettyRPC内部架设HTTP服务器，以网页的形式直观地展示监控数据信息。
* 为了提高JMX数据统计监控的精度，服务端采用了临界区对RPC请求进行隔离。但是如果客户端是通过AsyncInvoker异步调用的方式进行RPC请求的话，则会把异步并行加载强制转成异步串行加载。这并不是我们希望看到的。
* 后续会针对异步并行加载串行化的问题，在服务端采用哈希队列的方式隔离管理临界区对象，减少JMX监控对异步调用的侵蚀影响。

----------
## NettyRPC 2.6 Build 2017/10/30

**在NettyRPC 2.5的基础上，针对JMX监控统计RPC调用指标的场景，采用临界区哈希分片加锁算法，降低锁的颗粒度，减少JMX监控对RPC调用的影响。**
* 哈希分片加锁算法，采用空间换时间策略。其主要思路参考借鉴了JDK中ConcurrentHashMap的实现，经过优化之后，NettyRPC的JMX监控统计性能会有很大的提升。
* 哈希分片加锁算法中，哈希分片的个数，通过环境变量nettyrpc.jmx.metrics.hash.nums进行设定，默认为8个哈希桶。当然，你可以改成其它大于1的整数，数值越大，哈希冲突越小，JMX监控的性能越好，但是代价是，JVM堆内存空间有所损耗。
* 经过哈希分片加锁算法优化之后，特别是对异步并行调用（AsyncInvoker）的侵蚀大幅度降低，对整个NettyRPC系统而言，统计性能也有质的提升。一方面不影响原有系统的调用吞吐量，另外一方面也不会降低JMX的统计精度。
* 默认加锁采用非公平锁，你可以通过设置环境变量nettyrpc.jmx.metrics.lock.fair为1，改成公平锁。在高并发调用的场景，建议采用非公平锁。

----------
## NettyRPC 2.7 Build 2017/11/2

**在NettyRPC 2.6的基础上，新增内嵌HTTP服务器，用于展示NettyRPC模块调用监控指标。**
* 利用JMX接口，从NettyRPC服务端获取模块调用的监控数据。
* 在环境变量nettyrpc.jmx.invoke.metrics为1的前提下，在浏览器输入URL：http://ip地址:18886/NettyRPC.html/metrics。  即可查看NettyRPC系统模块间的调用统计情况。
* 可以按F5刷新统计界面，查看最新的统计指标。

----------
## NettyRPC 2.8 Build 2018/2/2

**在NettyRPC 2.7的基础上，加入RPC请求过滤器链和监听器链功能**
* 通过监听器链和过滤器链，可以对RPC客户端请求进行过滤和监听。具体参考[NettyRPC入门手册](https://github.com/tang-jie/NettyRPC/wiki/NettyRPC%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)中的“RPC请求链式处理”章节。
* 过滤器链封装类（com.newlandframework.rpc.filter.ModuleFilterChainWrapper）、监听器链封装类（com.newlandframework.rpc.listener.ModuleListenerChainWrapper）通过spring依赖注入。
* NettyRPC内置了一些链式过滤器：com.newlandframework.rpc.filter.support.ClassLoaderChainFilter、com.newlandframework.rpc.filter.support.EchoChainFilter，以及链式监听器：com.newlandframework.rpc.listener.support.ModuleListenerAdapter。具体可以根据需求进行扩展添加。

----------
## NettyRPC相关博客文章
if you want to know more details,okey!you can see my blog:

**Talk about how to use Netty nio frameworks development high performance RPC server**

**谈谈如何使用Netty开发实现高性能的RPC服务器**

http://www.cnblogs.com/jietang/p/5615681.html

**By Netty to realize high performance RPC server optimization of the message serialization**

**Netty实现高性能RPC服务器优化篇之消息序列化**

http://www.cnblogs.com/jietang/p/5675171.html

**Based on Netty to develop RPC server design experience**

**基于Netty打造RPC服务器设计经验谈**

http://www.cnblogs.com/jietang/p/5983038.html

----------

## Author
唐洁（tangjie） http://www.cnblogs.com/jietang/

----------

## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
