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

import java.io.IOException;
import org.eclipse.jetty.nosql.key_value.AbstractKeyValueStoreClient;
import org.eclipse.jetty.nosql.key_value.KeyValueStoreSessionIdManager;
import org.eclipse.jetty.nosql.key_value.redis.jedis.JedisClientFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 *
 * @author den
 */
public class RedisSessionIdManager extends KeyValueStoreSessionIdManager {
    private final static Logger log = Log.getLogger(RedisSessionIdManager.class);
    private AbstractRedisClientFactory _clientFactory = null;

    public RedisSessionIdManager(Server server) throws IOException {
        this(server, "127.0.0.1:6379");
    }

    public RedisSessionIdManager(Server server, String serverString) throws IOException {
        this(server, serverString, null);
    }

    public RedisSessionIdManager(Server server, String serverString, AbstractRedisClientFactory cf) throws IOException {
        super(server, serverString);
        _clientFactory = cf;
    }

    @Override
    protected void doStart() throws Exception {
        log.info("starting...");
        super.doStart();
        log.info("started.");
    }

    @Override
    protected void doStop() throws Exception {
        log.info("stopping...");
        super.doStop();
        log.info("stopped.");
    }

    @Override
    protected AbstractKeyValueStoreClient newClient(String serverString) {
        synchronized (this) {
            if (_clientFactory == null) {
                _clientFactory = new JedisClientFactory(); // default client
            }
        }
        AbstractKeyValueStoreClient client = _clientFactory.create(serverString);
        client.setTimeout(getTimeout());
        return client;
    }

    public AbstractRedisClientFactory getClientFactory() {
        return _clientFactory;
    }

    public void setClientFactory(AbstractRedisClientFactory cf) {
        synchronized (this) {
            _clientFactory = cf;
        }
    }

}
