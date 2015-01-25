package org.eclipse.jetty.nosql.key_value.session.serializable;

import org.eclipse.jetty.nosql.key_value.session.AbstractSessionFactory;
import org.eclipse.jetty.nosql.key_value.session.SerializableSession;
import org.eclipse.jetty.nosql.key_value.session.Serializer;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;

public class NativeSerializationSessionFactory extends AbstractSessionFactory {
    public NativeSerializationSessionFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public NativeSerializationSessionFactory(ClassLoader cl) {
        super(new NativeSerializer(cl));
    }

    public NativeSerializableSession create() {
        return new NativeSerializableSession();
    }

    @Override
    public byte[] pack(SerializableSession session, Serializer tc) throws SerializationException {
        byte[] raw = null;
        try {
            raw = tc.encode(session);
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return raw;
    }


    @Override
    public NativeSerializableSession unpack(byte[] raw, Serializer tc) {
        NativeSerializableSession session = null;
        try {
            session = tc.decode(raw, NativeSerializableSession.class);
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return session;
    }

    @Override
    public void setClassLoader(ClassLoader cl) {
        NativeSerializer tc = new NativeSerializer(cl);
        transcoder = tc;
    }
}
