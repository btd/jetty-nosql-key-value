package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoLastAccessTimeTest extends AbstractRedisLastAccessTimeTest
{
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new KryoRedisTestServer(port,max,scavenge);
    }
}
