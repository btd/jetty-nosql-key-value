package org.eclipse.jetty.nosql.key_value.session.kryo;

import org.eclipse.jetty.nosql.key_value.session.SerializableSession;
import org.eclipse.jetty.nosql.key_value.session.AbstractSessionFactory;
import org.eclipse.jetty.nosql.key_value.session.Serializer;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;

public class KryoSessionFactory extends AbstractSessionFactory {
    public KryoSessionFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public KryoSessionFactory(ClassLoader cl) {
        super(new KryoSerializer(cl));
    }

    public SerializableSession create() {
        return new KryoSession();
    }

    @Override
    public byte[] pack(SerializableSession session, Serializer tc) throws SerializationException {
        byte[] raw = null;
        try {
            raw = tc.encode(session);
        } catch (Exception error) {
            throw(new SerializationException(error));
        }
        return raw;
    }

    @Override
    public SerializableSession unpack(byte[] raw, Serializer tc) throws SerializationException {
        SerializableSession session = null;
        try {
            session = tc.decode(raw, KryoSession.class);
        } catch (Exception error) {
            throw(new SerializationException(error));
        }
        return session;
    }

    @Override
    public void setClassLoader(ClassLoader cl) {
        KryoSerializer tc = new KryoSerializer(cl);
        transcoder = tc;
    }
}
