package org.eclipse.jetty.nosql.key_value.session.xstream;

import org.eclipse.jetty.nosql.key_value.session.AbstractSessionFactory;
import org.eclipse.jetty.nosql.key_value.session.SerializableSession;
import org.eclipse.jetty.nosql.key_value.session.Serializer;
import org.eclipse.jetty.nosql.key_value.session.SerializationException;

public class XStreamSessionFactory extends AbstractSessionFactory {
    public XStreamSessionFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public XStreamSessionFactory(ClassLoader cl) {
        super(new XStreamSerializer(cl));
    }

    public SerializableSession create() {
        return new XStreamSession();
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
    public SerializableSession unpack(byte[] raw, Serializer tc) throws SerializationException {
        SerializableSession session = null;
        try {
            session = tc.decode(raw, XStreamSession.class);
        } catch (Exception error) {
            throw (new SerializationException(error));
        }
        return session;
    }

    @Override
    public void setClassLoader(ClassLoader cl) {
        XStreamSerializer tc = new XStreamSerializer(cl);
        transcoder = tc;
    }
}

