/**
 * @filename:KryoSerialize.java
 *
 * Newland Co. Ltd. All rights reserved.
 *
 * @Description:Kryo序列化/反序列化实现
 * @author tangjie
 * @version 1.0
 *
 */
package newlandframework.netty.rpc.serialize.support.kryo;

import newlandframework.netty.rpc.serialize.support.RpcSerialize;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KryoSerialize implements RpcSerialize {

    private KryoPool pool = null;

    public KryoSerialize(final KryoPool pool) {
        this.pool = pool;
    }

    public void serialize(OutputStream output, Object object) throws IOException {
        Kryo kryo = pool.borrow();
        Output out = new Output(output);
        kryo.writeClassAndObject(out, object);
        out.close();
        output.close();
        pool.release(kryo);
    }

    public Object deserialize(InputStream input) throws IOException {
        Kryo kryo = pool.borrow();
        Input in = new Input(input);
        Object result = kryo.readClassAndObject(in);
        in.close();
        input.close();
        pool.release(kryo);
        return result;
    }
}
