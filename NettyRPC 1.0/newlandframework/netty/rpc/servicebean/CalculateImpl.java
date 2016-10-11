/**
 * @filename:CalculateImpl.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:计算器定义接口实现
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.servicebean;

public class CalculateImpl implements Calculate {
    //两数相加
    public int add(int a, int b) {
        return a + b;
    }
}
