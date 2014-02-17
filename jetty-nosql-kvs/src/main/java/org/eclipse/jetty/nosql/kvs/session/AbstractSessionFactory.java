package org.eclipse.jetty.nosql.kvs.session;


import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractSessionFactory {
	protected final static Logger log = Log.getLogger(AbstractSessionFactory.class);
	public abstract ISerializableSession create();
	protected ISerializationTranscoder transcoder;

	public AbstractSessionFactory(ISerializationTranscoder t) {
		transcoder = t;
	}

	public ISerializableSession create(String sessionId) {
		ISerializableSession s = create();
		s.setId(sessionId);
		return s;
	}

	public ISerializableSession create(String sessionId, long created) {
		ISerializableSession s = create(sessionId);
		s.setCreationTime(created);
		return s;
	}

	public ISerializableSession create(String sessionId, long created, long accessed) {
		ISerializableSession s = create(sessionId, created);
		s.setAccessed(accessed);
		return s;
	}
        
        public ISerializableSession create(String sessionId, long created, long accessed, int maxIdle) {
		ISerializableSession s = create(sessionId, created, accessed);
		s.setMaxIdle(maxIdle);
		return s;
	}

	public ISerializableSession create(AbstractSession session) {
		synchronized(session) {
			ISerializableSession s = create(session.getClusterId(), session.getCreationTime(), session.getAccessed(), session.getMaxInactiveInterval());
			if (!session.isValid()) {
				// we do not need to retrieve attributes of invalidated sessions since
				// they have been cleared on AbstractSession.invalidate().
				s.setValid(false);
			}
			return s;
		}
	}

	public ISerializationTranscoder getTranscoder() {
		return transcoder;
	}

		public byte[] pack(ISerializableSession session) {
		return pack(session, getTranscoder());
	}

	public abstract byte[] pack(ISerializableSession session, ISerializationTranscoder tc) throws TranscoderException;

	public ISerializableSession unpack(byte[] raw) {
		return unpack(raw, getTranscoder());
	}

	public abstract ISerializableSession unpack(byte[] raw, ISerializationTranscoder tc) throws TranscoderException;

	public abstract void setClassLoader(ClassLoader cl);
}
