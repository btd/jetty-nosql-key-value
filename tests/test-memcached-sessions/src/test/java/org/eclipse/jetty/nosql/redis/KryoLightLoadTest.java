package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class KryoLightLoadTest extends AbstractRedisLightLoadTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new KryoRedisTestServer(port);
    }
}
