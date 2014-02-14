package org.eclipse.jetty.nosql.redis;

import org.eclipse.jetty.server.session.AbstractTestServer;

public class XStreamLightLoadTest extends AbstractRedisLightLoadTest
{
    @Override
    public AbstractTestServer createServer(int port)
    {
        return new XStreamRedisTestServer(port);
    }
}
