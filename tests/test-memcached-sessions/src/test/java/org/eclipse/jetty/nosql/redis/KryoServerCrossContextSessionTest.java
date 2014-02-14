package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoServerCrossContextSessionTest extends AbstractRedisServerCrossContextSessionTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoRedisTestServer(port);
    }
}
