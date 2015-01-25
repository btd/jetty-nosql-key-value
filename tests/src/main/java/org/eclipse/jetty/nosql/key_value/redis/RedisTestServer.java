/*
 * Copyright 2014 den.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.jetty.nosql.key_value.redis;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.nosql.key_value.KeyValueStoreSessionIdManager;
import org.eclipse.jetty.nosql.key_value.SessionDump;
import org.eclipse.jetty.nosql.key_value.redis.jedis.JedisClientFactory;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.AbstractTestServer;
import org.eclipse.jetty.server.session.SessionHandler;

/**
 * @version $Revision$ $Date$
 */
public class RedisTestServer extends AbstractTestServer {

    protected KeyValueStoreSessionIdManager _idManager;
    protected boolean _saveAllAttributes = false; // false save dirty, true save all

    public RedisTestServer(int port) {
        this(port, 30, 10);
    }

    public RedisTestServer(int port, int maxInactivePeriod, int scavengePeriod) {
        this(port, maxInactivePeriod, scavengePeriod, System.getProperty("org.eclipse.jetty.nosql.redis.servers"));
    }

    public RedisTestServer(int port, int maxInactivePeriod, int scavengePeriod, String sessionIdMgrConfig) {
        super(port, maxInactivePeriod, scavengePeriod, sessionIdMgrConfig);
    }

    public RedisTestServer(int port, int maxInactivePeriod, int scavengePeriod, boolean saveAllAttributes) {
        this(port, maxInactivePeriod, scavengePeriod);
        _saveAllAttributes = saveAllAttributes;
    }

    public SessionIdManager newSessionIdManager(String config) {
        if (config == null) {
            config = "127.0.0.1:6379";
        }
        if (_idManager != null) {
            try {
                _idManager.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            _idManager.setScavengePeriod((int) TimeUnit.SECONDS.toMillis(_scavengePeriod));
            _idManager.setWorkerName("node0");

            try {
                _idManager.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return _idManager;
        }

        try {
            System.err.println("RedisTestServer:SessionIdManager:" + _maxInactivePeriod + "/" + _scavengePeriod);
            AbstractRedisClientFactory clientFactory = getRedisClientFactory();
            if (clientFactory == null) {
                _idManager = new RedisSessionIdManager(_server, config);
            } else {
                _idManager = new RedisSessionIdManager(_server, config, clientFactory);
            }
            _idManager.setScavengePeriod((int) TimeUnit.SECONDS.toMillis(_scavengePeriod));
            _idManager.setKeyPrefix("RedisTestServer::");
            _idManager.setKeySuffix("::RedisTestServer");
            // to avoid stupid bugs of instance initialization...
            _idManager.setScavengePeriod(_idManager.getDefaultExpiry());
            _idManager.setServerString(_idManager.getServerString());
            _idManager.setTimeout(_idManager.getTimeout());

            return _idManager;
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    public SessionManager newSessionManager() {
        RedisSessionManager manager;
        try {
            manager = new RedisSessionManager();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        manager.setSavePeriod(1);
        manager.setStalePeriod(0);
        manager.setSaveAllAttributes(_saveAllAttributes);
        //manager.setScavengePeriod((int)TimeUnit.SECONDS.toMillis(_scavengePeriod));
        return manager;
    }

    public SessionHandler newSessionHandler(SessionManager sessionManager) {
        return new SessionHandler(sessionManager);
    }

    public static void main(String... args) throws Exception {
        RedisTestServer server8080 = new RedisTestServer(8080);
        server8080.addContext("/").addServlet(SessionDump.class, "/");
        server8080.start();

        RedisTestServer server8081 = new RedisTestServer(8081);
        server8081.addContext("/").addServlet(SessionDump.class, "/");
        server8081.start();

        server8080.join();
        server8081.join();
    }

    public AbstractRedisClientFactory getRedisClientFactory() {
        AbstractRedisClientFactory clientFactory = new JedisClientFactory();

        return clientFactory;
    }
}
