package org.eclipse.jetty.nosql.key_value.redis.kryo;

import org.eclipse.jetty.nosql.key_value.redis.KryoRedisTestServer;
import org.eclipse.jetty.nosql.key_value.redis.AbstractRedisServerCrossContextSessionTest;
import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoServerCrossContextSessionTest extends AbstractRedisServerCrossContextSessionTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoRedisTestServer(port);
    }
}
