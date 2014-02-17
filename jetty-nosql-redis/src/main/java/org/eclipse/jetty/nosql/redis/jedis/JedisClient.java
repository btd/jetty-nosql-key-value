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

package org.eclipse.jetty.nosql.redis.jedis;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.jetty.nosql.kvs.KeyValueStoreClientException;
import org.eclipse.jetty.nosql.redis.AbstractRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

/**
 *
 * @author den
 */
public class JedisClient extends AbstractRedisClient {
    private JedisPool _client = null;
    
    public JedisClient() {
        this("localhost:6379");
    }

    public JedisClient(String serverString) {
        super(serverString);
    }

    @Override
    public boolean establish() throws KeyValueStoreClientException {
        if (_client != null) {
            shutdown();
        }
        String[] splittedAddress = this._serverString.split(":");
        _client = new JedisPool(splittedAddress[0], Integer.parseInt(splittedAddress[1]));
        return true;
    }

    @Override
    public boolean shutdown() throws KeyValueStoreClientException {
        if(_client != null) {
            try {
                _client.destroy();
            } catch(JedisException je) {
                throw new KeyValueStoreClientException(je);
            } finally {
                _client = null;
            }
        }
        return true;
    }

    @Override
    public boolean isAlive() {
        return this._client != null;
    }

    @Override
    public byte[] get(String key) throws KeyValueStoreClientException {
        if (!isAlive()) {
            throw(new KeyValueStoreClientException(new IllegalStateException("client not established")));
        }
        Jedis jedis = _client.getResource();
        byte[] raw = null;
        try {
            raw = jedis.get(key.getBytes());
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
        return raw;
    }

    @Override
    public boolean set(String key, byte[] raw) throws KeyValueStoreClientException {
        if (!isAlive()) {
            throw(new KeyValueStoreClientException(new IllegalStateException("client not established")));
        }
        Jedis jedis = _client.getResource();
        try {
            jedis.set(key.getBytes(), raw);
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
        return true;
    }

    @Override
    public boolean set(String key, byte[] raw, int exp) throws KeyValueStoreClientException {
        if (!isAlive()) {
            throw(new KeyValueStoreClientException(new IllegalStateException("client not established")));
        }
        Jedis jedis = _client.getResource();
        try {
            jedis.setex(key.getBytes(), exp, raw);
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
        return true;
    }

    @Override
    public boolean add(String key, byte[] raw) throws KeyValueStoreClientException {
        Jedis jedis = _client.getResource();
        try {
            Long r = jedis.setnx(key.getBytes(), raw);
            return r.longValue() == 1;
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
    }

    @Override
    public boolean add(String key, byte[] raw, int exp) throws KeyValueStoreClientException {
        Jedis jedis = _client.getResource();
        try {
            Transaction tr = jedis.multi();
            byte[] _key = key.getBytes();
            Response<Long> r = tr.setnx(_key, raw);
            tr.expire(_key, exp);
            tr.exec();
            return r.get() == 1;
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
    }

    @Override
    public boolean delete(String key) throws KeyValueStoreClientException {
        Jedis jedis = _client.getResource();
        try {
            jedis.del(key);
        } catch(JedisException je) {
            _client.returnBrokenResource(jedis);
            throw new KeyValueStoreClientException(je);
        } finally {
            _client.returnResource(jedis);
        }
        return true;
    }
}
