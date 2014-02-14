package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoImmortalSessionTest extends AbstractRedisClientCrossContextSessionTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoRedisTestServer(port);
    }
}
