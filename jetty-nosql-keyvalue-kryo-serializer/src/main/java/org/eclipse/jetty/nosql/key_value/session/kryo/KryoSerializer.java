package org.eclipse.jetty.nosql.key_value.session.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;
import org.eclipse.jetty.nosql.key_value.session.Serializer;

public class KryoSerializer implements Serializer {
    private Kryo kryo = null;

    public KryoSerializer() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public KryoSerializer(ClassLoader cl) {
        kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setClassLoader(cl);
    }

    public byte[] encode(Object obj) throws SerializationException {
        byte[] raw = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Output output = new Output(stream);
            kryo.writeObject(output, obj);
            output.close();
            raw = stream.toByteArray();
        } catch (Exception error) {
            throw(new SerializationException(error));
        }
        return raw;
    }

    public <T> T decode(byte[] raw, Class<T> klass) throws SerializationException {
        T obj = null;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(raw);
            Input input = new Input(stream);
            obj = kryo.readObject(input, klass);
        } catch (Exception error) {
            throw(new SerializationException(error));
        }
        return obj;
    }
}
