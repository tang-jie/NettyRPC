/**
 * @filename:HessianSerializePool.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Hessian序列化/反序列化池
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.hessian;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class HessianSerializePool {

    private GenericObjectPool<HessianSerialize> hessianPool;
    private static HessianSerializePool poolFactory = null;

    private HessianSerializePool() {
        hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());
    }

    public static HessianSerializePool getHessianPoolInstance() {
        if (poolFactory == null) {
            synchronized (HessianSerializePool.class) {
                if (poolFactory == null) {
                    poolFactory = new HessianSerializePool();
                }
            }
        }
        return poolFactory;
    }

    public HessianSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        hessianPool = new GenericObjectPool<HessianSerialize>(new HessianSerializeFactory());
        
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        
        hessianPool.setConfig(config);
    }

    public HessianSerialize borrow() {
        try {
            return getHessianPool().borrowObject();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void restore(final HessianSerialize object) {
        getHessianPool().returnObject(object);
    }

    public GenericObjectPool<HessianSerialize> getHessianPool() {
        return hessianPool;
    }
}
