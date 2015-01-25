package org.eclipse.jetty.nosql.key_value.session;


import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractSessionFactory {
    protected final static Logger log = Log.getLogger(AbstractSessionFactory.class);

    public abstract SerializableSession create();

    protected Serializer transcoder;

    public AbstractSessionFactory(Serializer t) {
        transcoder = t;
    }

    public SerializableSession create(String sessionId) {
        SerializableSession s = create();
        s.setId(sessionId);
        return s;
    }

    public SerializableSession create(String sessionId, long created) {
        SerializableSession s = create(sessionId);
        s.setCreationTime(created);
        return s;
    }

    public SerializableSession create(String sessionId, long created, long accessed) {
        SerializableSession s = create(sessionId, created);
        s.setAccessed(accessed);
        return s;
    }

    public SerializableSession create(String sessionId, long created, long accessed, int maxIdle) {
        SerializableSession s = create(sessionId, created, accessed);
        s.setMaxIdle(maxIdle);
        return s;
    }

    public SerializableSession create(AbstractSession session) {
        synchronized (session) {
            SerializableSession s = create(session.getClusterId(), session.getCreationTime(), session.getAccessed(), session.getMaxInactiveInterval());
            if (!session.isValid()) {
                // we do not need to retrieve attributes of invalidated sessions since
                // they have been cleared on AbstractSession.invalidate().
                s.setValid(false);
            }
            return s;
        }
    }

    public Serializer getTranscoder() {
        return transcoder;
    }

    public byte[] pack(SerializableSession session) {
        return pack(session, getTranscoder());
    }

    public abstract byte[] pack(SerializableSession session, Serializer tc) throws SerializationException;

    public SerializableSession unpack(byte[] raw) {
        return unpack(raw, getTranscoder());
    }

    public abstract SerializableSession unpack(byte[] raw, Serializer tc) throws SerializationException;

    public abstract void setClassLoader(ClassLoader cl);
}
