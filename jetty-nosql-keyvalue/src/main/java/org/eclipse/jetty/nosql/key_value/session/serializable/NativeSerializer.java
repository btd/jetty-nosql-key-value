package org.eclipse.jetty.nosql.key_value.session.serializable;

import org.eclipse.jetty.nosql.key_value.session.Serializer;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NativeSerializer implements Serializer {
    private ClassLoader classLoader = null;

    public NativeSerializer() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public NativeSerializer(ClassLoader cl) {
        classLoader = cl;
    }

    public byte[] encode(Object obj) throws SerializationException {
        if (obj == null) {
            return null;
        }
        byte[] raw = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            raw = baos.toByteArray();
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return raw;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] raw, Class<T> klass) throws SerializationException {
        if (raw == null) {
            return null;
        }
        Object obj = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(raw);
            ObjectInputStream ois = new ClassLoadingObjectInputStream(bais, classLoader);
            obj = ois.readObject();
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return (T) obj;
    }
}
