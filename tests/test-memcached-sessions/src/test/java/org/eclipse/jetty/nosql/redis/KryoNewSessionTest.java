package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoNewSessionTest extends AbstractRedisNewSessionTest
{
    @Override
    public AbstractTestServer createServer(int port, int max, int scavenge)
    {
        return new KryoRedisTestServer(port,max,scavenge);
    }
}
