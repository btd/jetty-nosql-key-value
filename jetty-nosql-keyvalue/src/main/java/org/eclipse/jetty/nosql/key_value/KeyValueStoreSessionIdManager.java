package org.eclipse.jetty.nosql.key_value;

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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class KeyValueStoreSessionIdManager extends AbstractSessionIdManager {
    private int timeout = 1000;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private final static Logger log = Log.getLogger(KeyValueStoreSessionIdManager.class);

    final static long __defaultScavengePeriod = 30 * 60 * 1000; // every 30 minutes

    private long _scavengePeriod = __defaultScavengePeriod;

    protected Server _server;

    protected String _keyPrefix = "";
    protected String _keySuffix = "";
    protected KeyValueStoreClient _client = null;
    protected String _serverString = "";

    public KeyValueStoreSessionIdManager(Server server, String serverString) {
        super(new Random());
        this._serverString = serverString;
        _server = server;
    }

    protected abstract AbstractKeyValueStoreClient newClient(String serverString);

    /* ------------------------------------------------------------ */

    /**
     * The period in seconds between scavenge checks.
     *
     * @param scavengePeriod
     */
    public void setScavengePeriod(long scavengePeriod) {
        if (scavengePeriod <= 0) {
            _scavengePeriod = __defaultScavengePeriod;
        } else {
            _scavengePeriod = TimeUnit.SECONDS.toMillis(scavengePeriod);
        }
    }

    /* ------------------------------------------------------------ */
    @Override
    protected void doStart() throws Exception {
        log.info("starting...");
        super.doStart();
        _client = newClient(_serverString);
        if (_client == null) {
            throw new IllegalStateException("newClient(" + _serverString + ") returns null.");
        }
        log.info("use " + _client.getClass().getSimpleName() + " as client factory.");
        _client.connect();
        log.info("started.");
    }

    /* ------------------------------------------------------------ */
    @Override
    protected void doStop() throws Exception {
        log.info("stopping...");
        if (_client != null) {
            _client.shutdown();
            _client = null;
        }
        super.doStop();
        log.info("stopped.");
    }

    public void removeSession(HttpSession session) {}

    public void addSession(HttpSession session) {}

    /* ------------------------------------------------------------ */

    /**
     * is the session id known to key_value, and is it valid
     */
    @Override
    public boolean idInUse(final String idInCluster) {
        byte[] data = getKey(idInCluster);

        return data != null;
    }

     /* ------------------------------------------------------------ */
    @Override
    public void invalidateAll(final String sessionId) {
        // tell all contexts that may have a session object with this id to
        // get rid of them
        Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);
        for (int i = 0; contexts != null && i < contexts.length; i++) {
            SessionHandler sessionHandler = ((ContextHandler) contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
                SessionManager manager = sessionHandler.getSessionManager();
                if (manager != null && manager instanceof KeyValueStoreSessionManager) {
                    ((KeyValueStoreSessionManager) manager).invalidateSession(sessionId);
                }
            }
        }
    }

    public void expireAll(String sessionId) {
        //tell all contexts that may have a session object with this id to
        //get rid of them
        Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);
        for (int i = 0; contexts != null && i < contexts.length; i++) {
            SessionHandler sessionHandler = ((ContextHandler) contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
                SessionManager manager = sessionHandler.getSessionManager();

                if (manager != null && manager instanceof KeyValueStoreSessionManager) {
                    ((KeyValueStoreSessionManager) manager).expire(sessionId);
                }
            }
        }
    }

    @Override
    public void renewSessionId(final String oldClusterId, final String oldNodeId, final HttpServletRequest request) {
        //generate a new id
        String newClusterId = newSessionId(request.hashCode());

        //tell all contexts to update the id 
        Handler[] contexts = _server.getChildHandlersByClass(ContextHandler.class);
        for (int i = 0; contexts != null && i < contexts.length; i++) {
            SessionHandler sessionHandler = ((ContextHandler) contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
                SessionManager manager = sessionHandler.getSessionManager();

                if (manager != null && manager instanceof KeyValueStoreSessionManager) {
                    ((KeyValueStoreSessionManager) manager).renewSessionId(oldClusterId, oldNodeId, newClusterId,
                            getNodeId(newClusterId, request));
                }
            }
        }
    }


    protected String mangleKey(final String key) {
        return _keyPrefix + key + _keySuffix;
    }

    protected byte[] getKey(final String idInCluster) {
        log.debug("get: id=" + idInCluster);
        byte[] raw = null;
        try {
            raw = _client.get(mangleKey(idInCluster));
        } catch (KeyValueStoreClientException error) {
            log.warn("unable to get key: id=" + idInCluster, error);
        }
        return raw;
    }

    protected boolean setKey(final String idInCluster, final byte[] raw) {
        return setKey(idInCluster, raw, getDefaultExpiry());
    }

    protected boolean setKey(final String idInCluster, final byte[] raw, int expiry) {
        if (expiry < 0) {
            expiry = 0; // 0 means forever
        }
        log.debug("set: id=" + idInCluster + ", expiry=" + expiry);
        boolean result = false;
        try {
            result = _client.set(mangleKey(idInCluster), raw, expiry);
        } catch (KeyValueStoreClientException error) {
            log.warn("unable to set key: id=" + idInCluster, error);
        }
        return result;
    }

    protected boolean addKey(final String idInCluster, final byte[] raw) {
        return addKey(idInCluster, raw, getDefaultExpiry());
    }

    protected boolean addKey(final String idInCluster, final byte[] raw, int expiry) {
        if (expiry < 0) {
            expiry = 0; // 0 means forever
        }
        log.debug("add: id=" + idInCluster + ", expiry=" + expiry);
        boolean result = false;
        try {
            result = _client.add(mangleKey(idInCluster), raw, expiry);
        } catch (KeyValueStoreClientException error) {
            log.warn("unable to add key: id=" + idInCluster, error);
        }
        return result;
    }

    protected boolean deleteKey(final String idInCluster) {
        log.debug("delete: id=" + idInCluster);
        boolean result = false;
        try {
            result = _client.delete(mangleKey(idInCluster));
        } catch (KeyValueStoreClientException error) {
            log.warn("unable to delete key: id=" + idInCluster, error);
        }
        return result;
    }

    public int getDefaultExpiry() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(_scavengePeriod);
    }

    public String getKeyPrefix() {
        return _keyPrefix;
    }

    public void setKeyPrefix(final String keyPrefix) {
        this._keyPrefix = keyPrefix;
    }

    public String getKeySuffix() {
        return _keySuffix;
    }

    public void setKeySuffix(final String keySuffix) {
        this._keySuffix = keySuffix;
    }

    public String getServerString() {
        return _serverString;
    }

    public void setServerString(final String serverString) {
        this._serverString = serverString;
    }



}
