package org.eclipse.jetty.nosql.kvs;

//========================================================================
//Copyright (c) 2011 Intalio, Inc.
//Copyright (c) 2012 Geisha Tokyo Entertainment, Inc.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//The Eclipse Public License is available at
//http://www.eclipse.org/legal/epl-v10.html
//The Apache License v2.0 is available at
//http://www.opensource.org/licenses/apache2.0.php
//You may elect to redistribute this code under either of these licenses.
//========================================================================
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.nosql.NoSqlSession;
import org.eclipse.jetty.nosql.NoSqlSessionManager;
import org.eclipse.jetty.nosql.kvs.session.AbstractSessionFactory;
import org.eclipse.jetty.nosql.kvs.session.ISerializableSession;
import org.eclipse.jetty.nosql.kvs.session.TranscoderException;
import org.eclipse.jetty.nosql.kvs.session.serializable.SerializableSessionFactory;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class KeyValueStoreSessionManager extends NoSqlSessionManager {

    private final static Logger log = Log.getLogger("org.eclipse.jetty.nosql.kvs.KeyValueStoreSessionManager");

    protected AbstractSessionFactory sessionFactory = null;

    /**
     * the context id is only set when this class has been started
     */
    private String _contextId = null;

    /* ------------------------------------------------------------ */
    public KeyValueStoreSessionManager() {
        super();
    }

    /*------------------------------------------------------------ */
    @Override
    public void doStart() throws Exception {
        log.info("starting...");
        super.doStart();
        String[] hosts = getContextHandler().getVirtualHosts();

        if (hosts == null || hosts.length == 0) {
            hosts = new String[]{"::"}; // IPv6 equiv of 0.0.0.0
        }
        String contextPath = getContext().getContextPath();
        if (contextPath == null || "".equals(contextPath)) {
            contextPath = "*";
        }

        _contextId = createContextId(hosts, contextPath);
        if (sessionFactory == null) {
            sessionFactory = new SerializableSessionFactory();
        }
        log.info("use " + sessionFactory.getClass().getSimpleName() + " as session factory.");
        sessionFactory.setClassLoader(_loader);
        
        log.info("started.");
    }

    @Override
    public void doStop() throws Exception {
        // override doStop() and invalidatedSessions() to skip invoking NoSqlSessionManager#invalidatedSeessions()
        // we do not want to invalidate all sessions on servlets restart.
        log.info("stopping...");
        super.doStop();
        log.info("stopped.");
    }

    /* ------------------------------------------------------------ */
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jetty.server.session.AbstractSessionManager#setSessionIdManager
     * (org.eclipse.jetty.server.SessionIdManager)
     */
    @Override
    public void setSessionIdManager(final SessionIdManager idManager) {
        try {
            KeyValueStoreSessionIdManager kvsIdManager = (KeyValueStoreSessionIdManager) idManager;
            super.setSessionIdManager(kvsIdManager);
        } catch (ClassCastException error) {
            log.warn("unable to cast " + idManager.getClass() + " to " + KeyValueStoreSessionIdManager.class + ".");
            throw (error);
        }
    }

    /* ------------------------------------------------------------ */
    @Override
    protected Object save(final NoSqlSession session, final Object version, final boolean activateAfterSave) {
        try {
            log.debug("save session {}", session.getClusterId());
            session.willPassivate();

            ISerializableSession data;
            if (session.isValid()) {
                long longVersion = 1; // default version for new sessions
                if (version != null) {
                    longVersion = (Long) version + 1L;
                    data = getKey(session.getId());
                    long currentMaxIdle = data.getMaxIdle();
                    if (getMaxInactiveInterval() > 0 && getMaxInactiveInterval() < currentMaxIdle) {
                        data.setMaxIdle(getMaxInactiveInterval());
                    }
                } else {
                    data = getSessionFactory().create(session);
                    data.setMaxIdle(getMaxInactiveInterval());
                }
                data.setContextVersion(_contextId, longVersion);
                data.setAccessed(session.getAccessed());

                Set<String> names = session.takeDirty();
                if (isSaveAllAttributes()) {
                    names.addAll(session.getNames()); // note dirty may include removed names
                }

                for (String name : names) {
                    data.setContextAttribute(_contextId, name, session.getAttribute(name));
                }
            } else {
                log.debug("save: skip saving invalidated session: id=" + session.getId());
                deleteKey(session.getId());
                return null;
            }

            try {
                if (!setKey(session.getClusterId(), data)) {
                    throw (new RuntimeException("unable to set key: data=" + data));
                }
            } catch (TranscoderException error) {
                throw (new IllegalArgumentException("unable to serialize session: id=" + session.getClusterId() + ", data="
                        + data, error));
            }
            log.debug("save:db.sessions.update(" + session.getClusterId() + "," + data + ")");

            if (activateAfterSave) {
                session.didActivate();
            }

            return version;
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }

    /*------------------------------------------------------------ */
    @Override
    protected Object refresh(final NoSqlSession session, Object version) {
        log.debug("refresh " + session);
        ISerializableSession data = null;
        try {
            data = getKey(session.getClusterId());
        } catch (TranscoderException error) {
            throw (new IllegalStateException("unable to deserialize session: id=" + session.getClusterId(), error));
        }
        // check if our in memory version is the same as what is on KVS
        if (version != null) {
            long saved = 0;
            if (data != null) {
                saved = data.getContextVersion(_contextId);

                if (saved == (Long) version) {
                    log.debug("refresh not needed");
                    return version;
                }
                version = Long.valueOf(saved);
            }
        }

        // If it doesn't exist, invalidate
        if (data == null) {
            log.debug("refresh:marking invalid, no object");
            session.invalidate();
            return null;
        }

        // If it has been flagged invalid, invalidate
        boolean valid = data.isValid();
        if (!valid) {
            log.debug("refresh:marking invalid, valid flag " + valid);
            session.invalidate();
            return null;
        }

        // We need to update the attributes. We will model this as a passivate,
        // followed by bindings and then activation.
        session.willPassivate();
        try {
            for (String name : data.getContextAttributes(_contextId).keySet()) {
                Object value = data.getContextAttribute(_contextId, name);
                // only bind value if it didn't exist in session
                if (!session.getNames().contains(name)) {
                    session.doPutOrRemove(name, value);
                    session.bindValue(name, value);
                } else {
                    session.doPutOrRemove(name, value);
                }
            }

            // cleanup, remove values from session, that don't exist in data anymore:
            for (String name : session.getNames()) {
                if (!data.getContextAttributes(_contextId).containsKey(name)) {
                    session.doPutOrRemove(name, null);
                    session.unbindValue(name, session.getAttribute(name));
                }
            }

            data.setAccessed(System.currentTimeMillis());

            try {
                if (!setKey(session.getClusterId(), data)) {
                    throw (new RuntimeException("unable to set key: data=" + data));
                }
            } catch (TranscoderException error) {
                throw (new IllegalArgumentException("unable to serialize session: id=" + session.getClusterId() + ", data="
                        + data, error));
            }

            session.didActivate();
            return version;
        } catch (Exception e) {
            log.warn(e);
        }

        return null;
    }

    /*------------------------------------------------------------ */
    @Override
    protected NoSqlSession loadSession(final String clusterId) {
        log.debug("loadSession: loading: id=" + clusterId);
        ISerializableSession data = getKey(clusterId);
        log.debug("loadSession: loaded: id=" + clusterId + ", data=" + data);

        if (data == null) {
            return null;
        }

        boolean valid = data.isValid();
        if (!valid) {
            log.debug("loadSession: id=" + clusterId + ", data=" + data + " has been invalidated.");
            return null;
        }

        if (!clusterId.equals(data.getId())) {
            log.warn("loadSession: invalid id (expected:" + clusterId + ", got:" + data.getId() + ")");
            return null;
        }

        Map<String, Object> attrs = data.getContextAttributes(_contextId);

        try {
            if (attrs == null) {
                log.debug("session  {} not present for context {}", clusterId, _contextId);
                return null;
            }

            long version = data.getContextVersion(_contextId);
            long created = data.getCreationTime();
            long accessed = data.getAccessed();

            NoSqlSession session = new NoSqlSession(this, created, accessed, clusterId, version);

            log.debug("attrs {}", data);
            for (Map.Entry<String, Object> entry : data.getContextAttributes(_contextId).entrySet()) {
                session.doPutOrRemove(entry.getKey(), entry.getValue());
                session.bindValue(entry.getKey(), entry.getValue());
            }

            session.didActivate();

            return session;
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }

    /*------------------------------------------------------------ */
    @Override
    protected boolean remove(final NoSqlSession session) {
        log.debug("remove:session {} for context {}", session.getClusterId(), _contextId);
        ISerializableSession data = getKey(session.getClusterId());
        boolean exists = data.removeContext(_contextId);
        if (exists) {
            try {
                if (!setKey(session.getClusterId(), data)) {
                    throw (new RuntimeException("unable to set key: data=" + data));
                }
            } catch (TranscoderException error) {
                throw (new IllegalArgumentException("unable to serialize session: id=" + session.getClusterId() + ", data="
                        + data, error));
            }
        }
        return exists;
    }

    @Override
    protected void expire(String idInCluster) {
        log.debug("expire session {} ", idInCluster);

        //Expire the session for this context
        super.expire(idInCluster);

        //If the outer session document has not already been marked invalid, do so.
        ISerializableSession data = getKey(idInCluster);

        if (data.isValid()) {
            deleteKey(idInCluster);
        }
    }
    
    /** 
     * Change the session id. Note that this will change the session id for all contexts for which the session id is in use.
     * @see org.eclipse.jetty.nosql.NoSqlSessionManager#update(org.eclipse.jetty.nosql.NoSqlSession, java.lang.String, java.lang.String)
     */
    @Override
    protected void update(NoSqlSession session, String newClusterId, String newNodeId) throws Exception
    {
        ISerializableSession data = getKey(session.getClusterId());
        data.setId(newClusterId);
        
        deleteKey(session.getClusterId());
        
        try {
                if (!setKey(newClusterId, data)) {
                    throw (new RuntimeException("unable to set key: data=" + data));
                }
            } catch (TranscoderException error) {
                throw (new IllegalArgumentException("unable to serialize session: id=" + newClusterId + ", data="
                        + data, error));
            }
    }

    @Override
    public void invalidateSession(String idInCluster) {
        // do nothing.
        // invalidated sessions will not save in KeyValueStoreSessionManager.save()
        log.debug("invalidateSession: invalidating " + idInCluster);
    }
    
    

    @Override
    protected boolean removeSession(String idInCluster) {
        return deleteKey(idInCluster);
    }
    
    private String createContextId(String[] virtualHosts, String contextPath)
    {
        String contextId = virtualHosts[0] + contextPath;
        
        contextId.replace('/', '_');
        contextId.replace('.','_');
        contextId.replace('\\','_');
        
        return contextId;
    }


    protected String mangleKey(final String idInCluster) {
        return idInCluster;
    }

    protected ISerializableSession getKey(final String idInCluster) throws TranscoderException {
        byte[] raw = ((KeyValueStoreSessionIdManager) _sessionIdManager).getKey(mangleKey(idInCluster));
        if (raw == null) {
            return null;
        } else {
            return getSessionFactory().unpack(raw);
        }
    }

    protected boolean setKey(final String idInCluster, final ISerializableSession data) throws TranscoderException {
        byte[] raw = getSessionFactory().pack(data);
        if (raw == null) {
            return false;
        } else {
            return ((KeyValueStoreSessionIdManager) _sessionIdManager).setKey(mangleKey(idInCluster), raw,
                    data.getMaxIdle());
        }
    }

    protected boolean addKey(final String idInCluster, final ISerializableSession data) throws TranscoderException {
        byte[] raw = getSessionFactory().pack(data);
        if (raw == null) {
            return false;
        } else {
            return ((KeyValueStoreSessionIdManager) _sessionIdManager).addKey(mangleKey(idInCluster), raw,
                    getMaxInactiveInterval());
        }
    }

    protected boolean deleteKey(final String idInCluster) {
        return ((KeyValueStoreSessionIdManager) _sessionIdManager).deleteKey(mangleKey(idInCluster));
    }


    public AbstractSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(final AbstractSessionFactory sf) {
        this.sessionFactory = sf;
    }

}
