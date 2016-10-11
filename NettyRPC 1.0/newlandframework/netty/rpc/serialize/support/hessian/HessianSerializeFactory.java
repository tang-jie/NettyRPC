/**
 * @filename:HessianSerializeFactory.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Hessian序列化/反序列化对象工厂池
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.hessian;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class HessianSerializeFactory extends BasePooledObjectFactory<HessianSerialize> {

    public HessianSerialize create() throws Exception {
        return createHessian();
    }

    public PooledObject<HessianSerialize> wrap(HessianSerialize hessian) {
        return new DefaultPooledObject<HessianSerialize>(hessian);
    }

    private HessianSerialize createHessian() {
        return new HessianSerialize();
    }
}
