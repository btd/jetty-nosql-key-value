package org.eclipse.jetty.nosql.key_value.redis.kryo;

import org.eclipse.jetty.nosql.key_value.redis.AbstractRedisRemoveSessionTest;
import org.eclipse.jetty.nosql.key_value.redis.KryoRedisTestServer;
import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoRemoveSessionTest extends AbstractRedisRemoveSessionTest
{ 
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new KryoRedisTestServer(port,max,scavenge);
    }
}
